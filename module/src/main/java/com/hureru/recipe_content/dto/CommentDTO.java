package com.hureru.recipe_content.dto;

import com.hureru.recipe_content.bean.Comment;
import lombok.Data;

/**
 * @author zheng
 */
@Data
public class CommentDTO {

    private String recipeId;
    // 食谱评分
    private Integer rating;
    // 使用ID关联作者
    private String authorId;

    // 支持评论嵌套，可选字段
    private String parentId;
    private String text;

}
