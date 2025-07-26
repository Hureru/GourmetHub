package com.hureru.recipe_content.service.Impl;

import com.hureru.recipe_content.bean.Comment;
import com.hureru.recipe_content.repository.CommentRepository;
import com.hureru.recipe_content.service.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author zheng
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {
    private final CommentRepository commentRepository;

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Optional<Comment> getCommentById(String id) {
        return commentRepository.findById(id);
    }

    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    public List<Comment> getCommentsByRecipeId(String recipeId) {
        return commentRepository.findByRecipeId(recipeId);
    }

    @Override
    public void deleteComment(String id) {
        commentRepository.deleteById(id);
    }
}
