package com.hureru.recipe_content.controller;

import com.hureru.common.R;
import com.hureru.common.Response;
import com.hureru.common.utils.JwtUtil;
import com.hureru.iam.dto.group.Update;
import com.hureru.recipe_content.bean.Recipe;
import com.hureru.recipe_content.dto.RecipeDTO;
import com.hureru.recipe_content.service.IRecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author zheng
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
public class RecipeController {
    private final IRecipeService recipeService;

    /**
     * 获取单个食谱
     */
    @GetMapping("/{id}")
    public R<Recipe> getRecipe(@PathVariable String id) {
        return R.ok(recipeService.getRecipeById(id));
    }

    /**
     * 添加食谱
     * @param recipeDTO 食谱数据
     * @return 添加的食谱
     */
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_recipe.create')")
    public R<Recipe> addRecipe(@AuthenticationPrincipal Jwt jwt, @RequestBody RecipeDTO recipeDTO) {
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        boolean isArtisan = JwtUtil.getIsArtisanFromJwt(jwt);
        Recipe recipe = recipeService.saveRecipe(recipeDTO, userId, isArtisan);
        return R.ok(recipe);
    }
    /**
     * 修改食谱
     * @param recipeDTO 食谱数据
     * @param recipeId 食谱id
     * @return 修改的食谱
     */
    @PatchMapping("/{recipeId}")
    @PreAuthorize("hasAuthority('SCOPE_recipe.update')")
    public R<Recipe> updateRecipe(@AuthenticationPrincipal Jwt jwt, @Validated(Update.class) @RequestBody RecipeDTO recipeDTO, @PathVariable String recipeId) {
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        Recipe recipe = recipeService.updateRecipe(recipeId, recipeDTO, userId);
        return R.ok(recipe);
    }

    /**
     * 发布/下架食谱
     * @param recipeId 食谱id
     * @param status 发布状态
     * @return 修改的食谱
     */
    @PatchMapping("/{recipeId}/publish/{status}")
    @PreAuthorize("hasAuthority('SCOPE_recipe.publish')")
    public R<Recipe> publishRecipe(@AuthenticationPrincipal Jwt jwt, @PathVariable String recipeId, @PathVariable Boolean status) {
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        Recipe recipe = recipeService.publishRecipe(recipeId, userId, status);
        return R.ok(recipe);
    }



    /**
     * 删除食谱
     * @param id 食谱id
     * @return 删除结果
     */
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('SCOPE_recipe.delete')")
    public Response deleteRecipe(@AuthenticationPrincipal Jwt jwt, String id) {
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        recipeService.deleteRecipe(id, userId);
        return Response.ok();
    }
}
