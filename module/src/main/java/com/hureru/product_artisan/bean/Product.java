package com.hureru.product_artisan.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zheng
 */
@Data
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    
    private String sku;
    
    private String name;
    private String description;
    private List<String> images;
    
    private String artisanId;
    private ArtisanInfo artisanInfo;
    
    private String categoryId;
    
    private Price price;
    private Integer stockQuantity;
    
    private List<String> tags;
    private List<Attribute> attributes;
    
    private Double ratingAverage;
    private Integer ratingCount;
    private Integer commentCount;
    
    private AuditInfo audit;
    private Boolean isPublished;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;

    // 内部类 ArtisanInfo
    @Data
    public static class ArtisanInfo {
        private String name;
        private String logoUrl;
    }

    // 内部类 Price
    @Data
    public static class Price {
        private Number amount;
        private String currency;
    }

    // 内部类 Attribute
    @Data
    public static class Attribute {
        private String key;
        private String name;
        private String value;
    }

    // 审核信息嵌入对象
    @Data
    public static class AuditInfo {
        private Status status = Status.PENDING; // 默认待审状态
        private LocalDateTime submitTime = LocalDateTime.now();
        private LocalDateTime reviewTime;
        private String reviewer;
        private String comment;

        // 枚举定义
        public enum Status {
            PENDING,
            APPROVED,
            REJECTED
        }
    }
}
