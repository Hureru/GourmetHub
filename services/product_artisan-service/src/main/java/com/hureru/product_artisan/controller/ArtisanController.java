package com.hureru.product_artisan.controller;

import com.hureru.common.R;
import com.hureru.common.utils.JwtUtil;
import com.hureru.iam.dto.group.ChildCreate;
import com.hureru.iam.dto.group.Update;
import com.hureru.product_artisan.bean.Artisan;
import com.hureru.product_artisan.dto.ArtisanDTO;
import com.hureru.product_artisan.service.IArtisanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 存储商家信息的表 前端控制器
 * </p>
 *
 * @author zheng
 * @since 2025-07-29
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
//@PreAuthorize("hasAuthority('SCOPE_artisan')")
public class ArtisanController {
    private final IArtisanService artisanService;

    /**
     * 仅限 OpenFeign_ iam-service 调用，添加商家信息
     * @param artisanDTO 商家信息
     * @return 商家信息
     */
    //TODO 权限配置: 使用 Getaway 网关配置允许访问服务
    @PostMapping("/internal/artisan")
    public Artisan addArtisan(@RequestBody @Validated(ChildCreate.class) ArtisanDTO artisanDTO) {
        log.debug("OpenFeign调用[addArtisan]:{}", artisanDTO);
        return artisanService.saveArtisan(artisanDTO);
    }

    @PreAuthorize("hasAuthority('SCOPE_artisans.get')")
    @GetMapping("/artisan/{id}")
    public Artisan getArtisanById(@PathVariable String id) {
        log.debug("[controller] getArtisanById:{}", id);
        return artisanService.getArtisanById(id);
    }


    /**
     * 获取所有待审核的商家
     */
    @PreAuthorize("hasAuthority('SCOPE_artisans.pendings')")
    @GetMapping("/artisan/pending")
    public R getPendingArtisans() {
        log.debug("[controller] getPendingArtisans.....");
        List<Artisan> artisans = artisanService.getPendingArtisans();
        return R.ok("success", artisans);
    }

    /**
     * 更新商家
     * @param artisanDTO 商家信息
     *                   仅限商家自己修改
     * @return {@code 200 OK}
     */
    @PreAuthorize("hasAuthority('SCOPE_artisans.update')")
    @PutMapping("/artisan/{id}")
    public R updateArtisan(@AuthenticationPrincipal Jwt jwt,  @PathVariable String id, @RequestBody @Validated(Update.class) ArtisanDTO artisanDTO) {
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        log.debug("[controller] updateArtisan:{}", artisanDTO);
        artisanService.updateArtisan(userId, id, artisanDTO);
        return R.ok();
    }

    /**
     * 删除商家
     * @param id 商家id
     * @return {@code 200 OK}
     */
    @PreAuthorize("hasAuthority('SCOPE_artisans.delete')")
    @DeleteMapping("/artisan/{id}")
    public R deleteArtisan(@PathVariable String id) {
        artisanService.deleteArtisan(id);
        return R.ok();
    }

}
