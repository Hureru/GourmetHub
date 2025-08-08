package com.hureru.product_artisan.dto;

import com.hureru.product_artisan.bean.Product;
import lombok.Data;

/**
 * @author zheng
 */
@Data
public class AuditDTO {
    Product.AuditInfo.Status status;
    String comment;
}
