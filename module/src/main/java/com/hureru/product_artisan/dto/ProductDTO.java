package com.hureru.product_artisan.dto;

import com.hureru.iam.dto.group.Create;
import com.hureru.iam.dto.group.Update;
import com.hureru.product_artisan.bean.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.util.List;

/**
 * @author zheng
 */
@Data
public class ProductDTO {
    @NotBlank(message = "SKU不能为空", groups = Create.class)
    @Null(message = "无法更新SKU", groups = Update.class)
    private String sku;

    private String name;
    private String description;
    private List<String> images;

    private String categoryId;

    private Product.Price price;
    private Integer stockQuantity;

    private List<String> tags;
    private List<Product.Attribute> attributes;

    /**
     * 是否在审核成功后立即发布
     */
    private Boolean isPublished;
}
