package com.hureru.product_artisan.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zheng
 */
@Data
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private ArtisanInfo artisanInfo;
    private String name;
    private String description;
    private String category;
    private List<String> images;
    private Price price;
    private Stock stock;
    private List<String> tags;
    private Map<String, Object> attributes;
    private Boolean isPublished;
    // 新增审核相关字段
    private AuditInfo audit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
//    @Version
    private Long version;
    // 内部类 ArtisanInfo
    @Data
    public static class ArtisanInfo {
        @Field("id")
        private String id;
        private String name;
    }

    // 内部类 Price
    @Data
    public static class Price {
        private Double amount;
        private String currency;
    }

    // 内部类 Stock
    @Data
    public static class Stock {
        private Integer quantity;
        private StockLevel level;

        public enum StockLevel {
            IN_STOCK,
            LOW_STOCK,
            OUT_OF_STOCK
        }
    }

    // 审核信息嵌入对象
    @Data
    public static class AuditInfo {
        private Status status = Status.PENDING; // 默认待审状态
        private LocalDateTime submitTime = LocalDateTime.now();
        private LocalDateTime reviewTime;
        private String reviewerId;
        private String comment;

        // 枚举定义
        public enum Status {
            PENDING,
            APPROVED,
            REJECTED;
        }
    }
}