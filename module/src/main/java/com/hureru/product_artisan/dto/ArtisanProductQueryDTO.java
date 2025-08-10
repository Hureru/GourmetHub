package com.hureru.product_artisan.dto;

import com.hureru.product_artisan.bean.Product;
import lombok.Data;

/**
 * @author zheng
 */
@Data
public class ArtisanProductQueryDTO {
    String sku;
    String name;

    String artisanId;
    String categoryId;

    Product.AuditInfo.Status status;
    Boolean isPublished;
}
