package com.hureru.product_artisan.bean;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author zheng
 */
@Data
@Document(collection = "categories")
public class Category {
    private String id;
    private String name;
    private String parentId;
}
