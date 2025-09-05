package com.hureru.recipe_content.repository;

import com.hureru.recipe_content.bean.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author zheng
 */
@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {
    Optional<Recipe> findRecipeByIsPublishedIsTrueAndId(String id);
    List<Recipe> findByTitle(String name);
}
