package com.hureru.recipe_content.repository;

import com.hureru.recipe_content.bean.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByRecipeId(String recipeId);
}
