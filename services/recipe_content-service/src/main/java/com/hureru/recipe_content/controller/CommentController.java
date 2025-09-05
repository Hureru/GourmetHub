package com.hureru.recipe_content.controller;

import com.hureru.common.PaginationData;
import com.hureru.common.R;
import com.hureru.common.utils.JwtUtil;
import com.hureru.recipe_content.bean.Comment;
import com.hureru.recipe_content.dto.CommentDTO;
import com.hureru.recipe_content.service.ICommentService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * @author zheng
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentController {
    private final ICommentService commentService;
    /**
     * 添加评论
     */
    @PostMapping("/comments")
    @PreAuthorize("hasAuthority('SCOPE_comment.create')")
    public R<Comment> addComment(@AuthenticationPrincipal Jwt jwt, CommentDTO commentDTO) {
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        Comment comment = commentService.saveComment(userId, commentDTO);
        return R.ok(comment);
    }

    /**
     * 分页获取食谱所有评论
     */
    @GetMapping("/recipes/{recipeId}/comments")
    public R<PaginationData<Comment>> getCommentsByRecipeId(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String recipeId,
            @Min(1) @RequestParam(defaultValue = "1") int page,
            @Min(5) @RequestParam(defaultValue = "10") int size) {
        PaginationData<Comment> comments = commentService.getCommentsByRecipeId(recipeId, page, size);
        return R.ok(comments);
    }
}
