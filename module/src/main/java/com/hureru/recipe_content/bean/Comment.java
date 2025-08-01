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
    // 使用ID关联作者
    private String authorId;
    private AuthorInfo authorInfo;

    // 支持评论嵌套，可选字段
    private String parentId;
    private String text;
    // 互动计数
    private Integer likeCount;
    private String createdAt;
    // 软删除标志
    private Boolean isDeleted;

    @Data
    public static class AuthorInfo {
        private String nickname;
        private String avatarUrl;
    }
}
