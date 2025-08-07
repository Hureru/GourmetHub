package com.hureru.product_artisan.dto;

import lombok.Data;

/**
 * @author zheng
 */
@Data
public class ProductQueryDTO {
    // 用于模糊查询产品名称
    private String name;
    // 用于精确匹配工匠ID
    private String artisanId;
    // 你可以根据需要添加更多查询字段，例如价格区间等
}
