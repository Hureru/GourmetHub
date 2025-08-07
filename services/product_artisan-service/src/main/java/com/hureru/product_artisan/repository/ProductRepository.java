package com.hureru.product_artisan.repository;

import com.hureru.product_artisan.bean.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zheng
 */
@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    // Spring Data MongoDB 会自动实现基本的 CRUD 操作
    // 你也可以添加自定义查询方法

    List<Product> findByName(String name);
    List<Product> findByPriceGreaterThan(Product.Price price);

    List<Product> findByIsPublished(boolean b);
}
