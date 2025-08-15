package com.hureru.product_artisan.service.Impl;

import com.hureru.common.PaginationData;
import com.hureru.common.exception.BusinessException;
import com.hureru.order.dto.StockDeductionRequest;
import com.hureru.product_artisan.bean.Artisan;
import com.hureru.product_artisan.bean.Product;
import com.hureru.product_artisan.dto.ArtisanProductQueryDTO;
import com.hureru.product_artisan.dto.AuditDTO;
import com.hureru.product_artisan.dto.ProductDTO;
import com.hureru.product_artisan.dto.ProductQueryDTO;
import com.hureru.product_artisan.repository.ArtisanRepository;
import com.hureru.product_artisan.repository.ProductRepository;
import com.hureru.product_artisan.service.IProductService;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author zheng
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final ProductRepository productRepository;
    private final ArtisanRepository artisanRepository;
    private final MongoTemplate mongoTemplate;
    private final RocketMQTemplate rocketMQTemplate;

    public static final String TOPIC = "COMPENSATE_STOCK_TOPIC";

    @Override
    public Product saveProduct(Long userId, ProductDTO productDTO) {
        Product product = new Product();
        product.setSku(productDTO.getSku());
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setImages(productDTO.getImages());
        product.setArtisanId(userId.toString());
        product.setCategoryId(productDTO.getCategoryId());

        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());

        product.setTags(productDTO.getTags());
        product.setAttributes(productDTO.getAttributes());
        // 为 True 代表 审核完成后立即发布
        product.setIsPublished(productDTO.getIsPublished());

        Optional<Artisan> artisanOpt = artisanRepository.findById(userId.toString());
        if (artisanOpt.isPresent()) {
            Artisan artisan = artisanOpt.get();
            Product.ArtisanInfo artisanInfo = new Product.ArtisanInfo();
            artisanInfo.setName(artisan.getName());
            artisanInfo.setLogoUrl(artisan.getLogoUrl());
            product.setArtisanInfo(artisanInfo);
        }

        product.setRatingAverage(4.5);
        product.setRatingCount(0);
        product.setCommentCount(0);

        product.setAudit(new Product.AuditInfo());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setVersion(1L);

        log.debug("[service] (saveProduct) addProduct: {}", product);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long userId, String id, ProductDTO productDTO) {
        Product product = productRepository.findById(id).orElseThrow(()-> new BusinessException(404, "产品不存在"));
        if (!product.getArtisanId().equals(userId.toString())){
            throw new BusinessException(403, "无权限修改产品");
        }

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setImages(productDTO.getImages());
        product.setCategoryId(productDTO.getCategoryId());

        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());

        product.setTags(productDTO.getTags());
        product.setAttributes(productDTO.getAttributes());

        product.setIsPublished(productDTO.getIsPublished());

        product.setUpdatedAt(LocalDateTime.now());

        log.debug("[service] (updateProduct) updateProduct: {}", product);
        return productRepository.save(product);
    }


    @Override
    public void approveProduct(Long userId, String id, AuditDTO auditDTO) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new BusinessException(404, "产品不存在");
        }
        if (!product.getAudit().getStatus().equals(Product.AuditInfo.Status.PENDING)) {
            throw new BusinessException(400, "产品审核状态错误");
        }
        product.getAudit().setStatus(auditDTO.getStatus());
        product.getAudit().setReviewTime(LocalDateTime.now());
        product.getAudit().setReviewer(String.valueOf(userId));
        product.getAudit().setComment(auditDTO.getComment());
        productRepository.save(product);
    }

    @Override
    public Product getProductById(String id) {
        log.debug("[service] (getProductById) id: {}", id);
         Product product = productRepository.findById(id).orElseThrow(() -> new BusinessException(404, "产品不存在"));
         if (!product.getIsPublished() || !product.getAudit().getStatus().equals(Product.AuditInfo.Status.APPROVED)) {
             throw new BusinessException(403, "产品未发布");
         }
        return product;
    }



    @Override
    public List<Product> getPublishedProducts() {
        return productRepository.findByIsPublished(true);
    }

    @Override
    public PaginationData<Product> searchProducts(ProductQueryDTO queryDTO, int page, int size, boolean isPublished) {
        // 1. 创建基础的过滤条件
        Criteria criteria;
        if (isPublished){
            criteria = new Criteria().andOperator(
                    Criteria.where("isPublished").is(true),
                    Criteria.where("audit.status").is(Product.AuditInfo.Status.APPROVED)
            );
        }else {
            criteria = new Criteria();
        }

        // 2. 动态构建查询条件
        if (queryDTO != null) {
            // 如果工匠ID不为空，则添加精确匹配条件
            if (StringUtils.hasText(queryDTO.getArtisanId())) {
                criteria.and("artisanId").is(queryDTO.getArtisanId());
            }

            // 如果产品名不为空，则使用文本搜索
            if (StringUtils.hasText(queryDTO.getName())) {
                // 使用 TextCriteria 进行全文搜索
                TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matchingPhrase(queryDTO.getName());
                criteria.andOperator(Criteria.where("$text").is(textCriteria));
            }
        }
        return getPageProduct(criteria, page, size);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public void deleteProduct(Long userId, String id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new BusinessException(404, "产品不存在"));
        if (!product.getArtisanId().equals(userId.toString())) {
            throw new BusinessException(403, "无删除权限");
        }
        productRepository.deleteById(id);
    }

    @Override
    public PaginationData<Product> getProductsByArtisanId(Long userId, ArtisanProductQueryDTO queryDTO, int page, int size) {
        queryDTO.setArtisanId(userId.toString());
        return getByArtisanProductQueryDTO(queryDTO, page, size);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(StockDeductionRequest request) {
        log.info("开始处理库存扣减, orderId: {}", request.getOrderSn());
        for (StockDeductionRequest.OrderItemDTO item : request.getItems()) {
            // 加乐观锁
            // 使用 MongoTemplate 实现库存扣减
            // 1. 先查出当前版本号
            Product product = mongoTemplate.findById(item.getProductId(), Product.class);
            if (product == null) {
                throw new BusinessException(404, "商品不存在: " + item.getProductId());
            }

            Long currentVersion = product.getVersion();

            // 2. 更新时加版本匹配条件
            Query query = new Query(Criteria.where("_id").is(item.getProductId())
                    .and("stockQuantity").gte(item.getQuantity())
                    // 乐观锁条件
                    .and("version").is(currentVersion));

            Update update = new Update()
                    .inc("stockQuantity", -item.getQuantity())
                    .inc("version", 1)
                    .set("updatedAt", LocalDateTime.now());

            UpdateResult result = mongoTemplate.updateFirst(query, update, Product.class);

            if (result.getModifiedCount() == 0) {
                // 库存不足或版本不匹配，扣减失败
                log.warn("库存扣减失败, 商品ID: {}, 数量: {}. 发送补偿消息.", item.getProductId(), item.getQuantity());
                // 发送补偿消息，通知订单服务取消订单
                rocketMQTemplate.syncSend(TOPIC, request.getOrderSn());
                // 抛出异常，确保当前事务回滚
                throw new BusinessException(500, "库存扣减失败，商品ID: " + item.getProductId());
            }
        }
        log.info("库存扣减成功, orderId: {}", request.getOrderSn());
        // 如果所有商品都扣减成功，事务将在此处提交
    }

    @Override
    public PaginationData<Product> getAllProducts(ArtisanProductQueryDTO queryDTO, int page, int size) {
        return getByArtisanProductQueryDTO(queryDTO, page, size);
    }

    private PaginationData<Product> getByArtisanProductQueryDTO(ArtisanProductQueryDTO queryDTO, int page, int size){
        Criteria criteria = new Criteria();
        if (queryDTO != null) {
            // 如果工匠ID不为空，则添加精确匹配条件
            if (StringUtils.hasText(queryDTO.getArtisanId())) {
                criteria.and("artisanId").is(queryDTO.getArtisanId());
            }

            // SKU 和 name 互斥
            if (StringUtils.hasText(queryDTO.getSku())) {
                criteria.and("sku").is(queryDTO.getSku());
            } else if (StringUtils.hasText(queryDTO.getName())) {
                // 如果产品名不为空，则使用文本搜索
                TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matchingPhrase(queryDTO.getName());
                criteria.andOperator(Criteria.where("$text").is(textCriteria));
            }

            // 分类
            if (StringUtils.hasText(queryDTO.getCategoryId())) {
                criteria.and("categoryId").is(queryDTO.getCategoryId());
            }

            // 审核状态
            if (queryDTO.getStatus() != null) {
                criteria.and("audit.status").is(queryDTO.getStatus());
            }

            // 是否 发布/审核后立即发布
            if (queryDTO.getIsPublished() != null){
                criteria.and("isPublished").is(queryDTO.getIsPublished());
            }
        }
        return getPageProduct(criteria, page, size);
    }

    private PaginationData<Product> getPageProduct(Criteria criteria, int page, int size){
        // 创建 Pageable 对象，可以添加默认排序
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        // 3. 创建 Query 对象
        Query query = new Query(criteria);

        // 3. 查询总记录数 (不含分页)
        long total = mongoTemplate.count(query, Product.class);

        // 4. 将分页信息添加到 Query 对象中
        query.with(pageable);

        // 5. 执行分页查询
        List<Product> list = mongoTemplate.find(query, Product.class);
        PageImpl<Product> productPage = new PageImpl<>(list, pageable, total);

        // 转换为自定义的 PaginationData 对象返回
        return new PaginationData<>(
                productPage.getContent(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.getNumber(),
                productPage.getSize()
        );
    }
}
