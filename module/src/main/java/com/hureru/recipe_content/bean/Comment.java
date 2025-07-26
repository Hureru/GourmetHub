package com.hureru.recipe_content.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author zheng
 */
@Data
@Document(collection = "comments")
public class Comment {
    @Id
    private String id;
    private String recipeId;
    private Author author;
    private String text;
    private String createdAt;

    @Data
    public static class Author {
        private String userId;
        private String nickname;
        private String avatarUrl;
    }
}
