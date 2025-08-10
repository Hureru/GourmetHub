package com.hureru.product_artisan.service;

import com.hureru.common.PaginationData;
import com.hureru.product_artisan.bean.Product;
import com.hureru.product_artisan.dto.AuditDTO;
import com.hureru.product_artisan.dto.ProductDTO;
import com.hureru.product_artisan.dto.ProductQueryDTO;

import java.util.List;

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
    /**
     * 更新产品信息
     * @param userId 用户ID
     * @param id 产品ID
     * @param productDTO 产品信息 DTO
     * @return 更新后的产品信息
     */
    Product updateProduct(Long userId, String id, ProductDTO productDTO);
    void approveProduct(Long userId, String id, AuditDTO auditDTO);
    Product getProductById(String id);
    List<Product> getAllProducts();
    List<Product> getPublishedProducts();
    /**
     * 根据动态条件分页查询产品
     * @param queryDTO 查询条件 DTO
     * @param page 页码
     * @param size 每页大小
     * @return 分页后的产品数据
     */
    PaginationData<Product> searchProducts(ProductQueryDTO queryDTO, int page, int size, boolean isPublished);
    List<Product> getProductsByName(String name);
    void deleteProduct(Long userId, String id);

    List<Product> getProductsByArtisanId(Long userId);
}
