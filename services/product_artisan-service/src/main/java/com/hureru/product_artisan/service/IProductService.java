package com.hureru.product_artisan.service;

import com.hureru.product_artisan.bean.Product;

import java.util.List;
import java.util.Optional;

/**
 * @author zheng
 */
public interface IProductService {
    Product saveProduct(Product product);
    Optional<Product> getProductById(String id);
    List<Product> getAllProducts();
    List<Product> getProductsByName(String name);
    void deleteProduct(String id);
}
