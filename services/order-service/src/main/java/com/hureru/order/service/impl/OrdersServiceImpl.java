package com.hureru.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hureru.common.exception.BusinessException;
import com.hureru.order.OrderStatus;
import com.hureru.order.bean.CartItems;
import com.hureru.order.bean.OrderItems;
import com.hureru.order.bean.Orders;
import com.hureru.order.dto.CreateOrderDirectlyDTO;
import com.hureru.order.dto.CreateOrderFromCartDTO;
import com.hureru.order.dto.OrderTransactionPayload;
import com.hureru.order.dto.StockDeductionRequest.OrderItemDTO;
import com.hureru.order.feign.AddressFeignClient;
import com.hureru.order.feign.ProductFeignClient;
import com.hureru.order.mapper.CartItemsMapper;
import com.hureru.order.mapper.OrderItemsMapper;
import com.hureru.order.mapper.OrdersMapper;
import com.hureru.order.service.ICartsService;
import com.hureru.order.service.IOrdersService;
import com.hureru.order.utils.OrderIdUtil;
import com.hureru.product_artisan.bean.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单主表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {
    private final CartItemsMapper cartItemsMapper;
    private final ProductFeignClient productFeignClient;
    private final AddressFeignClient addressFeignClient;
    private final OrderIdUtil orderIdUtil;
    private final RocketMQTemplate rocketMQTemplate;
    private final OrderItemsMapper orderItemsMapper;
    private final ICartsService cartsService;

    public static final String TX_ORDER_TOPIC = "TX_ORDER_TOPIC";

    @Override
    public String createOrderFromCart(Long userId, CreateOrderFromCartDTO dto) {
        // 1. 根据传入的 cartItemIds 查询购物车商品
        List<CartItems> cartItems = cartItemsMapper.selectBatchIds(dto.getCartItemIds());

        // 2. 校验
        if (CollectionUtils.isEmpty(cartItems)) {
            throw new BusinessException(404, "选择的商品不存在或已失效");
        }
        // 校验所有选中的商品是否都属于当前用户
        Long cartId = cartsService.getUserCart(userId);
        for (CartItems item : cartItems) {
            if (!item.getCartId().equals(cartId)) {
                throw new BusinessException(403, "包含不属于您的商品，操作非法");
            }
        }
        // 校验传入的ID数量和查出的数量是否一致
        if (cartItems.size() != dto.getCartItemIds().size()) {
            throw new BusinessException(404, "部分选择的商品不存在或已失效");
        }

        List<OrderItemDTO> orderItems = cartItems.stream()
                .map(item -> new OrderItemDTO(item.getProductId(), item.getQuantity()))
                .collect(Collectors.toList());

        String orderId = String.valueOf(orderIdUtil.nextId());

        // 3. 构造事务消息Payload
        OrderTransactionPayload payload = OrderTransactionPayload.builder()
                .userId(userId)
                .addressId(dto.getAddressId())
                .items(orderItems)
                // 标记为需要清除购物车项
                .clearCart(true)
                // 传递需要删除的项
                .cartItemIdsToDelete(dto.getCartItemIds())
                .orderSn(orderId)
                .build();

        // 4. 发送事务消息
        rocketMQTemplate.sendMessageInTransaction(
                TX_ORDER_TOPIC,
                MessageBuilder.withPayload(payload).build(),
                payload
        );

        return orderId;
    }

    @Override
    public String createOrderDirectly(Long userId, CreateOrderDirectlyDTO dto) {
        OrderItemDTO orderItem = new OrderItemDTO(dto.getProductId(), dto.getQuantity());
        String orderId = String.valueOf(orderIdUtil.nextId());

        // 1. 构造事务消息Payload
        OrderTransactionPayload payload = OrderTransactionPayload.builder()
                .userId(userId)
                .addressId(dto.getAddressId())
                .items(Collections.singletonList(orderItem))
                // 直接购买，不涉及购物车
                .clearCart(false)
                .orderSn(orderId)
                .build();

        // 2. 发送事务消息
        rocketMQTemplate.sendMessageInTransaction(
                TX_ORDER_TOPIC,
                MessageBuilder.withPayload(payload).build(),
                payload
        );

        return orderId;
    }

    @Override
    public Orders getOrderByOrderId(String orderSn) {
        return this.getOne(new LambdaQueryWrapper<Orders>().eq(Orders::getOrderSn, orderSn));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean executeCreateOrderTransaction(OrderTransactionPayload payload) {
        try {
            // 1. 远程调用商品服务，获取商品信息并校验价格和库存
            List<String> productIds = payload.getItems().stream().map(OrderItemDTO::getProductId).collect(Collectors.toList());
            List<Product> products = productFeignClient.getProductsByIds(productIds).getData();
            if (CollectionUtils.isEmpty(products) || products.size() != productIds.size()) {
                throw new BusinessException(404, "部分商品已下架或不存在");
            }
            Map<String, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, p -> p));

            BigDecimal totalPrice = BigDecimal.ZERO;
            for (OrderItemDTO itemDTO : payload.getItems()) {
                Product product = productMap.get(itemDTO.getProductId());
                BigDecimal priceAtPurchase = getPriceAtPurchase(itemDTO, product);

                totalPrice = totalPrice.add(priceAtPurchase.multiply(new BigDecimal(itemDTO.getQuantity())));
            }

            // 2. 创建订单 (Orders)
            Orders order = new Orders();
            order.setOrderSn(payload.getOrderSn());
            order.setUserId(payload.getUserId());
            order.setTotalAmount(totalPrice);
            // 初始状态为待处理
            order.setStatus(OrderStatus.PENDING);

            String address = addressFeignClient.getAddressById(payload.getAddressId());
            order.setShippingAddress(address);

            this.save(order);

            // 3. 创建订单项 (OrderItems)
            for (OrderItemDTO itemDTO : payload.getItems()) {
                Product product = productMap.get(itemDTO.getProductId());
                OrderItems orderItem = getOrderItems(itemDTO, order, product);
                orderItemsMapper.insert(orderItem);
            }

            // 4. 清空已结算的购物车项 (如果需要)
            if (payload.isClearCart() && !CollectionUtils.isEmpty(payload.getCartItemIdsToDelete())) {
                cartItemsMapper.deleteByIds(payload.getCartItemIdsToDelete());
            }
            log.info("本地事务执行成功, orderId: {}", payload.getOrderSn());
            return true;
        } catch (Exception e) {
            log.error("执行创建订单本地事务失败, payload: {}", payload, e);
            // 抛出异常，让上层@Transactional感知以回滚
            throw new BusinessException(500, "创建订单失败: " + e.getMessage());
        }
    }

    private static OrderItems getOrderItems(OrderItemDTO itemDTO, Orders order, Product product) {
        OrderItems orderItem = new OrderItems();
        orderItem.setOrderId(order.getId());
        orderItem.setProductId(itemDTO.getProductId());
        orderItem.setProductSnapshot(JSON.toJSONString(product));
        orderItem.setQuantity(itemDTO.getQuantity());
        // 单价
        Number amount = product.getPrice().getAmount();
        BigDecimal priceAtPurchase = BigDecimal
                // 先转 long，避免精度丢失
                .valueOf(amount.longValue())
                // 除以100得到元，保留2位小数
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        orderItem.setPriceAtPurchase(priceAtPurchase);
        orderItem.setShippingStatus(OrderStatus.AWAITING_PAYMENT);
        return orderItem;
    }

    private static BigDecimal getPriceAtPurchase(OrderItemDTO itemDTO, Product product) {
        if (product == null) {
            throw new BusinessException(404, "商品 " + itemDTO.getProductId() + " 不存在");
        }
        if (product.getStockQuantity() < itemDTO.getQuantity()) {
            throw new BusinessException(400, "商品 '" + product.getName() + "' 库存不足");
        }

        Number amount = product.getPrice().getAmount();
        return BigDecimal
                // 先转 long，避免精度丢失
                .valueOf(amount.longValue())
                // 除以100得到元，保留2位小数
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderId) {
        Orders order = this.getOrderByOrderId(orderId);
        if (order != null && order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CANCELLED);
            order.setUpdatedAt(LocalDateTime.now());
            this.updateById(order);
            log.info("订单已取消(因库存扣减失败), orderId: {}", orderId);
        }
    }
}
