package com.hureru.product_artisan.service.Impl;

import com.hureru.product_artisan.bean.Artisan;
import com.hureru.product_artisan.bean.Product;
import com.hureru.product_artisan.dto.ProductDTO;
import com.hureru.product_artisan.dto.ProductQueryDTO;
import com.hureru.product_artisan.repository.ArtisanRepository;
import com.hureru.product_artisan.repository.ProductRepository;
import com.hureru.product_artisan.service.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;
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
        product.setIsPublished(false);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setVersion(1L);

        log.debug("[service] (saveProduct) addProduct: {}", product);
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
        // 1. 创建基础的过滤条件
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("isPublished").is(isPublished),
                Criteria.where("audit.status").is(Product.AuditInfo.Status.APPROVED)
        );

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

        // 3. 创建 Query 对象
        Query query = new Query(criteria);

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
