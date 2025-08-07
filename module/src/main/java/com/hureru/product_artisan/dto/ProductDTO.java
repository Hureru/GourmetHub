package com.hureru.product_artisan.dto;

import com.hureru.product_artisan.bean.Product;
import lombok.Data;

import java.util.List;

/**
 * @author zheng
 */
@Data
public class ProductDTO {
    private String sku;

    private String name;
    private String description;
    private List<String> images;

    private String artisanId;
    private String categoryId;

    private Product.Price price;
    private Integer stockQuantity;

    private List<String> tags;
    private List<Product.Attribute> attributes;

    private Boolean isPublished;
}
