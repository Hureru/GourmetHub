package com.hureru.product_artisan.service;

import com.hureru.product_artisan.bean.Product;
import com.hureru.product_artisan.bean.Product.AuditInfo.Status;
import com.hureru.product_artisan.dto.AuditDTO;
import com.hureru.product_artisan.dto.ProductDTO;
import com.hureru.product_artisan.dto.ProductQueryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * @author zheng
 */
public interface IProductService {
    /**
     * 保存产品信息
     * @param userId 用户ID
     * @param productDTO 产品信息 DTO
     * @return 保存后的产品信息
     */
    Product saveProduct(Long userId, ProductDTO productDTO);
    void approveProduct(Long userId, String id, AuditDTO auditDTO);
    Optional<Product> getProductById(String id);
    List<Product> getAllProducts();
    List<Product> getPublishedProducts();
    /**
     * 根据动态条件分页查询产品
     * @param queryDTO 查询条件 DTO
     * @param pageable 分页信息
     * @return 分页后的产品数据
     */
    Page<Product> searchProducts(ProductQueryDTO queryDTO, Pageable pageable, boolean isPublished);
    List<Product> getProductsByName(String name);
    void deleteProduct(String id);
}
