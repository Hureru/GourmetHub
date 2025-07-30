package com.hureru.product_artisan.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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
    private List<String> certifications;
    private String createdAt;
    private String updatedAt;

    public Artisan(String id, String name, String brandStory, Location location, String logoUrl, List<String> certifications) {
        this.setId(id);
        this.setName(name);
        this.setBrandStory(brandStory);
        this.setLocation(location);
        this.setLogoUrl(logoUrl);
        this.setCertifications(certifications);
    }

    public Artisan() {

    }

    // 内部类 Location
    @Data
    public static class Location {
        private String city;
        private String state;
        private String country;
    }
}
