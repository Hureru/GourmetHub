package com.hureru.recipe_content.repository;

import com.hureru.recipe_content.bean.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zheng
 */
@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    Page<Comment> findByRecipeId(String recipeId, Pageable pageable);
}
