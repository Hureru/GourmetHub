package com.hureru.product_artisan.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author zheng
 */
@Data
@Document(collection = "artisans")
public class Artisan {
    @Id
    private String id;
    private String name;
    private String brandStory;
    private Location location;
    private String logoUrl;
    private String[] certifications;
    private String createdAt;
    private String updatedAt;

    // 内部类 Location
    @Data
    public static class Location {
        private String city;
        private String state;
        private String country;
    }
}
