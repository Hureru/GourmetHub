package com.hureru.product_artisan.service.Impl;

import com.hureru.product_artisan.bean.Product;
import com.hureru.product_artisan.dto.ProductQueryDTO;
import com.hureru.product_artisan.repository.ProductRepository;
import com.hureru.product_artisan.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author zheng
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final ProductRepository productRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getPublishedProducts() {
        return productRepository.findByIsPublished(true);
    }

    @Override
    public Page<Product> searchProducts(ProductQueryDTO queryDTO, Pageable pageable, boolean isPublished) {
        // 1. 创建 Query 对象，用于封装所有查询条件
        Query query = new Query();
        query.addCriteria(Criteria.where("isPublished").is(isPublished));

        // 2. 动态构建查询条件
        if (queryDTO != null) {
            // 如果产品名不为空，则添加模糊查询条件
            if (StringUtils.hasText(queryDTO.getName())) {
                // 使用正则表达式进行模糊匹配，i 表示不区分大小写
                query.addCriteria(Criteria.where("name").regex(queryDTO.getName(), "i"));
            }
            // 如果工匠ID不为空，则添加精确匹配条件
            if (StringUtils.hasText(queryDTO.getArtisanId())) {
                query.addCriteria(Criteria.where("artisanId").is(queryDTO.getArtisanId()));
            }
        }

        // 3. 查询总记录数 (不含分页)
        long total = mongoTemplate.count(query, Product.class);

        // 4. 将分页信息添加到 Query 对象中
        query.with(pageable);

        // 5. 执行分页查询
        List<Product> list = mongoTemplate.find(query, Product.class);

        // 6. 封装成 Page 对象返回
        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
}
