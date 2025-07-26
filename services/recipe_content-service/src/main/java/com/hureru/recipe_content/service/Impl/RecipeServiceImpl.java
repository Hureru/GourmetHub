package com.hureru.recipe_content.service.Impl;

import com.hureru.recipe_content.bean.Recipe;
import com.hureru.recipe_content.repository.RecipeRepository;
import com.hureru.recipe_content.service.IRecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author zheng
 */
@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements IRecipeService {
    private final RecipeRepository recipeRepository;

    @Override
    public Recipe saveRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    @Override
    public Optional<Recipe> getRecipeById(String id) {
        return recipeRepository.findById(id);
    }

    @Override
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    @Override
    public List<Recipe> getRecipesByTitle(String title) {
        return recipeRepository.findByTitle(title);
    }

    @Override
    public void deleteRecipe(String id) {
        recipeRepository.deleteById(id);
    }
}
