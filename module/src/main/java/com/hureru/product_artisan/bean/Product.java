package com.hureru.product_artisan.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private Date createdAt;
    private Date updatedAt;

    // 内部类 ArtisanInfo
    @Data
    public static class ArtisanInfo {
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

}