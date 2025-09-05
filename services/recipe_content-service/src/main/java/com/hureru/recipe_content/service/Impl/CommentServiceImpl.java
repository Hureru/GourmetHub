package com.hureru.recipe_content.service.Impl;

import com.hureru.common.PaginationData;
import com.hureru.recipe_content.bean.Comment;
import com.hureru.recipe_content.dto.CommentDTO;
import com.hureru.recipe_content.repository.CommentRepository;
import com.hureru.recipe_content.service.ICommentService;
import com.hureru.recipe_content.service.IRecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author zheng
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {
    private final CommentRepository commentRepository;
    private final IRecipeService recipeService;

    @Override
    public Comment saveComment(Long userId, CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setRecipeId(commentDTO.getRecipeId());
        comment.setAuthorId(userId.toString());
        comment.setParentId(commentDTO.getParentId());
        comment.setText(commentDTO.getText());
        comment.setLikeCount(0);
        comment.setCreatedAt(LocalDateTime.now().toString());
        recipeService.updateRecipeRating(commentDTO.getRecipeId(), commentDTO.getRating());
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
    public PaginationData<Comment> getCommentsByRecipeId(String recipeId, int page, int size) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByRecipeId(recipeId, pageable);
        return new PaginationData<>(
                commentPage.getContent(),
                commentPage.getTotalElements(),
                commentPage.getTotalPages(),
                page,
                size
        );
    }

    @Override
    public void deleteComment(String id) {
        commentRepository.deleteById(id);
    }
}
