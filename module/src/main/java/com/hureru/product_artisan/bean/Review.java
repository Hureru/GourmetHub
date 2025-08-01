package com.hureru.product_artisan.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @author zheng
 */
@Data
@Document(collection = "reviews")
public class Review {
    private String id;

    private String productId;
    private String sku;

    private String authorId;

    private Integer rating;
    private String comment;
    private List<String> images;

    private String parentId;

    private Integer likes;

    private Boolean isDeleted;

    private Date createdAt;
    private Date updatedAt;
}
