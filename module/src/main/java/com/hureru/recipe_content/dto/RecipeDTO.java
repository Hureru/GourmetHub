package com.hureru.recipe_content.dto;

import com.hureru.common.annotation.AtLeastOneField;
import com.hureru.iam.dto.group.Create;
import com.hureru.iam.dto.group.Update;
import com.hureru.recipe_content.bean.Recipe;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.util.List;

/**
 * @author zheng
 */
@Data
@AtLeastOneField(groups = Update.class, message = "至少需要一个更新字段")
public class RecipeDTO {
    private String title;
    private String description;
    private String mainImageUrl;

    private int prepTimeMinutes;
    private int cookTimeMinutes;
    private int servings;
    private String difficulty;

    private List<Recipe.Ingredient> ingredients;
    private List<Recipe.Step> steps;
    private List<String> tags;
}
