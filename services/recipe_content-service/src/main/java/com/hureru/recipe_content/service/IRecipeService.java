package com.hureru.recipe_content.service;


import com.hureru.recipe_content.bean.Recipe;
import com.hureru.recipe_content.dto.RecipeDTO;

/**
 * @author zheng
 */
public interface IRecipeService {
    Recipe getRecipeById(String id);

    Recipe saveRecipe(RecipeDTO recipeDTO, Long userId, boolean isArtisan);
    /**
     * 更新食谱评分
     */
    void updateRecipeRating(String recipeId, Integer rating);
    Recipe publishRecipe(String recipeId, Long userId, Boolean status);
    Recipe updateRecipe (String recipeId, RecipeDTO recipeDTO, Long userId);
    void deleteRecipe(String id, Long userId);

}
