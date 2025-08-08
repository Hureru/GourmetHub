package com.hureru.product_artisan.controller;

import com.hureru.common.PaginationData;
import com.hureru.common.R;
import com.hureru.common.Response;
import com.hureru.common.utils.JwtUtil;
import com.hureru.product_artisan.bean.Product;
import com.hureru.product_artisan.dto.AuditDTO;
import com.hureru.product_artisan.dto.ProductDTO;
import com.hureru.product_artisan.dto.ProductQueryDTO;
import com.hureru.product_artisan.service.IProductService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * @author zheng
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProductController {
    private final IProductService productService;

    /**
     * 根据动态条件分页搜索产品
     * @param queryDTO 查询条件，通过请求体传入
     * @param page 当前页码 从 1 开始
     * @param size 每页数量 最小为 5
     * @return 分页后的产品数据
     */
    @PostMapping("/public/products")
    public R<PaginationData<Product>> searchProducts(
            @RequestBody ProductQueryDTO queryDTO, // 从请求体获取查询条件
            @Min(1) @RequestParam(defaultValue = "1") int page,
            @Min(5) @RequestParam(defaultValue = "10") int size) {

        // 创建 Pageable 对象，可以添加默认排序
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        // 调用新的 service 方法
        Page<Product> productPage = productService.searchProducts(queryDTO, pageable, true);

        // 转换为自定义的 PaginationData 对象
        PaginationData<Product> paginationData = new PaginationData<>(
                productPage.getContent(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.getNumber(),
                productPage.getSize()
        );

        log.debug("[controller] searchProducts.....");
        return R.ok("ok", paginationData);
    }

    /**
     * 添加产品 需要 商家 权限
     * @param jwt 用户令牌
     * @param productDTO 产品数据
     *
     * @return {@code 200 OK}新增的产品数据
     */
    @PreAuthorize("hasAuthority('SCOPE_products.add')")
    @PostMapping("/products")
    public R<Product> addProduct(@AuthenticationPrincipal Jwt jwt, @RequestBody ProductDTO productDTO) {
        log.debug("[controller] addProduct.....");
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        Product product = productService.saveProduct(userId, productDTO);
        return R.ok("ok", product);
    }

    /**
     * 审核产品 需要 审核/管理员 权限
     * @param jwt 用户令牌
     * @param id 产品ID
     * @param auditDTO 审核信息
     * @return {@code 200 OK}
     */
    @PreAuthorize("hasAuthority('SCOPE_products.approve')")
    @PostMapping("/products/{id}/approve")
    public Response approveProduct(@AuthenticationPrincipal Jwt jwt, @PathVariable String id, @RequestBody AuditDTO auditDTO) {
        log.debug("[controller] approveProduct.....");
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        productService.approveProduct(userId, id, auditDTO);
        return Response.ok();
    }
}
