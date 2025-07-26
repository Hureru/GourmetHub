package com.hureru.recipe_content.service;

import com.hureru.recipe_content.bean.Comment;

import java.util.List;
import java.util.Optional;

/**
 * @author zheng
 */
public interface ICommentService {
    Comment saveComment(Comment comment);
    Optional<Comment> getCommentById(String id);
    List<Comment> getAllComments();
    List<Comment> getCommentsByRecipeId(String recipeId);
    void deleteComment(String id);
}
