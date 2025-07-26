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
    private Author author;
    private String description;
    private String mainImageUrl;
    private int prepTimeMinutes;
    private int cookTimeMinutes;
    private int servings;
    private String difficulty;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private List<String> tags;
    private Ratings ratings;
    private List<Comment> latestComments;
    private boolean isPublished;
    private Date publishedAt;
    private Date createdAt;
    private Date updatedAt;

    @Data
    public static class Author {
        private String userId;
        private String nickname;
    }

    @Data
    public static class Ingredient {
        private String name;
        private String quantity;
        private String notes;
        private String productId; // 可选，关联到平台上的具体产品
    }

    @Data
    public static class Step {
        private int stepNumber;
        private String instruction;
    }

    @Data
    public static class Ratings {
        private double average;
        private int count;
    }

    @Data
    public static class Comment {
        // 根据实际需求添加评论相关字段
    }
}
