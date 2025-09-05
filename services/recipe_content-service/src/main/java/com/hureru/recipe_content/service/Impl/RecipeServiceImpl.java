package com.hureru.recipe_content.service.Impl;

import com.hureru.common.exception.BusinessException;
import com.hureru.recipe_content.bean.Recipe;
import com.hureru.recipe_content.dto.RecipeDTO;
import com.hureru.recipe_content.feign.ArtisanFeignClient;
import com.hureru.recipe_content.feign.UserProfileFeignClient;
import com.hureru.recipe_content.repository.RecipeRepository;
import com.hureru.recipe_content.service.IRecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author zheng
 */
@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements IRecipeService {
    private final RecipeRepository recipeRepository;
    private final UserProfileFeignClient userProfileFeignClient;
    private final ArtisanFeignClient artisanFeignClient;

    @Override
    public Recipe getRecipeById(String id) {
        return recipeRepository.findRecipeByIsPublishedIsTrueAndId(id).orElseThrow(() -> new BusinessException(404, "食谱不存在"));
    }

    @Override
    public Recipe saveRecipe(RecipeDTO recipeDTO, Long userId, boolean isArtisan) {
        Recipe recipe = new Recipe();
        recipe.setTitle(recipeDTO.getTitle());
        recipe.setDescription(recipeDTO.getDescription());
        recipe.setMainImageUrl(recipeDTO.getMainImageUrl());
        recipe.setAuthorId(userId.toString());
        Recipe.AuthorInfo authorInfo = null;
        if (isArtisan) {
            authorInfo = artisanFeignClient.getAuthorInfo(userId).getData();
        }else {
            authorInfo = userProfileFeignClient.getUserInfo(userId).getData();
        }
        recipe.setAuthorInfo(authorInfo);
        recipe.setPrepTimeMinutes(recipeDTO.getPrepTimeMinutes());
        recipe.setCookTimeMinutes(recipeDTO.getCookTimeMinutes());
        recipe.setServings(recipeDTO.getServings());
        recipe.setDifficulty(recipeDTO.getDifficulty());
        recipe.setIngredients(recipeDTO.getIngredients());
        recipe.setSteps(recipeDTO.getSteps());
        recipe.setTags(recipeDTO.getTags());
        recipe.setRatingAverage(0.0);
        recipe.setRatingCount(0);
        recipe.setCommentCount(0);
        recipe.setIsPublished(false);
        recipe.setCreatedAt(LocalDateTime.now().toString());
        recipe.setUpdatedAt(LocalDateTime.now().toString());
        recipe.setPublishedAt(null);

        return recipeRepository.save(recipe);
    }

    @Override
    public void updateRecipeRating(String recipeId, Integer rating) {
        // 更新食谱总评分
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new BusinessException(404, "食谱不存在"));
        recipe.setRatingAverage((recipe.getRatingAverage() * recipe.getRatingCount() + rating) / (recipe.getRatingCount() + 1));
        recipe.setRatingCount(recipe.getRatingCount() + 1);
        recipe.setCommentCount(recipe.getCommentCount() + 1);
        recipeRepository.save(recipe);
    }


    @Override
    public Recipe publishRecipe(String recipeId, Long userId, Boolean status) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new BusinessException(404, "食谱不存在"));
        if (!recipe.getAuthorId().equals(userId.toString())) {
            throw new BusinessException(403, "无修改权限");
        } else if (recipe.getIsPublished().equals(status)){
            throw new BusinessException(400, "当前状态与目标状态一致");
        }
        recipe.setIsPublished(status);
        recipe.setPublishedAt(LocalDateTime.now().toString());
        return recipeRepository.save(recipe);
    }

    // 预缓存字段描述符（避免每次反射）
    private static final List<PropertyDescriptor> DTO_FIELDS =
            Arrays.stream(BeanUtils.getPropertyDescriptors(RecipeDTO.class))
                    .filter(pd -> !"class".equals(pd.getName()))
                    .toList();

    @Override
    public Recipe updateRecipe(String recipeId, RecipeDTO recipeDTO, Long userId) {
        // 查找现有食谱
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);

        if (recipeOptional.isEmpty()) {
            // "食谱不存在"
            throw new BusinessException(404, "食谱不存在");
        } else if (!recipeOptional.get().getAuthorId().equals(userId.toString())){
            // "无权限修改"
            throw new BusinessException(403, "无修改权限");
        }

        // 获取现有实体
        Recipe recipe = recipeOptional.get();

        // 使用缓存字段描述符更新字段
        DTO_FIELDS.forEach(pd -> {
            try {
                Object value = pd.getReadMethod().invoke(recipeDTO);
                if (value != null) {
                    // 使用反射设置对应字段的值
                    PropertyDescriptor targetPd = new PropertyDescriptor(pd.getName(), Recipe.class);
                    targetPd.getWriteMethod().invoke(recipe, value);
                }
            } catch (Exception ignored) {
                // 忽略无法设置的字段
            }
        });

        // 保存更新后的实体
        return recipeRepository.save(recipe);
    }

    @Override
    public void deleteRecipe(String id, Long userId) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new BusinessException(404, "食谱不存在"));
        if (!recipe.getAuthorId().equals(userId.toString())) {
            throw new BusinessException(403, "无删除权限");
        }
        recipeRepository.deleteById(id);
    }
}
