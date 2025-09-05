package com.hureru.recipe_content.service;

import com.hureru.common.PaginationData;
import com.hureru.recipe_content.bean.Comment;
import com.hureru.recipe_content.dto.CommentDTO;

import java.util.List;
import java.util.Optional;

/**
 * @author zheng
 */
public interface ICommentService {
    Comment saveComment(Long userId, CommentDTO commentDTO);
    Optional<Comment> getCommentById(String id);
    List<Comment> getAllComments();
    PaginationData<Comment> getCommentsByRecipeId(String recipeId, int page, int size);
    void deleteComment(String id);
}
