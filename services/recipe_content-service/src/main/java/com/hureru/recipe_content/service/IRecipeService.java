package com.hureru.recipe_content.service;


import com.hureru.recipe_content.bean.Recipe;

import java.util.List;
import java.util.Optional;

/**
 * @author zheng
 */
public interface IRecipeService {
    Recipe saveRecipe(Recipe recipe);
    Optional<Recipe> getRecipeById(String id);
    List<Recipe> getAllRecipes();
    List<Recipe> getRecipesByTitle(String title);
    void deleteRecipe(String id);
}
