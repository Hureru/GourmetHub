package com.hureru.recipe_content.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @author zheng
 */
@Data
@Document(collection = "recipes")
public class Recipe {
    @Id
    private String id;
    private String title;
    private String description;
    private String mainImageUrl;

    private String authorId;
    private AuthorInfo authorInfo;

    private Integer prepTimeMinutes;
    private Integer cookTimeMinutes;
    private Integer servings;
    private String difficulty;

    private List<Ingredient> ingredients;
    private List<Step> steps;
    private List<String> tags;

    private Double ratingAverage;
    private Integer ratingCount;
    // 新增评论数字段
    private Integer commentCount;

    private Boolean isPublished;
    private String publishedAt;
    private String createdAt;
    private String updatedAt;

    @Data
    public static class AuthorInfo {
        private String nickname;
        private String avatarUrl;
    }

    @Data
    public static class Ingredient {
        private String name;
        private Double amount; // 更改为数值类型
        private String unit;   // 新增单位字段
        private String notes;
        private String productId; // 可选，关联到平台上的具体产品
    }

    @Data
    public static class Step {
        private Integer stepNumber;
        private String instruction;
        private String imageUrl; // 新增图片URL字段
    }

}
