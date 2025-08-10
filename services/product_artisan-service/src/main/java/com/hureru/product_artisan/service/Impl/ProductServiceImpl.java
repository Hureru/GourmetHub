package com.hureru.product_artisan.service.Impl;

import com.hureru.common.PaginationData;
import com.hureru.common.exception.BusinessException;
import com.hureru.product_artisan.bean.Artisan;
import com.hureru.product_artisan.bean.Product;
import com.hureru.product_artisan.dto.AuditDTO;
import com.hureru.product_artisan.dto.ProductDTO;
import com.hureru.product_artisan.dto.ProductQueryDTO;
import com.hureru.product_artisan.repository.ArtisanRepository;
import com.hureru.product_artisan.repository.ProductRepository;
import com.hureru.product_artisan.service.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
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
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getPublishedProducts() {
        return productRepository.findByIsPublished(true);
    }

    @Override
    public PaginationData<Product> searchProducts(ProductQueryDTO queryDTO, int page, int size, boolean isPublished) {
        // 创建 Pageable 对象，可以添加默认排序
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        // 1. 创建基础的过滤条件
        Criteria criteria = null;
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
    public List<Product> getProductsByArtisanId(Long userId) {
        return productRepository.findByArtisanId(userId.toString());
    }
}
