package com.hureru.product_artisan.controller;

import com.hureru.common.R;
import com.hureru.common.Response;
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
     * 内部接口 仅限 OpenFeign_ iam-service 调用，添加商家信息
     * @param artisanDTO 商家信息
     * @return 商家信息
     */
    //TODO 权限配置: 使用 Getaway 网关配置允许访问服务
    @PostMapping("/internal/artisan")
    public Artisan addArtisan(@RequestBody @Validated(ChildCreate.class) ArtisanDTO artisanDTO) {
        log.debug("OpenFeign调用[addArtisan]:{}", artisanDTO);
        return artisanService.saveArtisan(artisanDTO);
    }

    /**
     * 获取 已发布商家 或 商家本人 信息，需要 用户/商家 权限
     *
     * @param jwt jwt
     * @param id 商家id
     * @return 商家信息
     */
    @PreAuthorize("hasAuthority('SCOPE_artisans.get')")
    @GetMapping("/artisan/{id}")
    public Artisan getArtisanById(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        log.debug("[controller] getArtisanById:{}", id);
        return artisanService.getArtisanById(id, userId);
    }


    /**
     * 获取所有待审核的商家 需要 管理员/审核员 权限
     */
    @PreAuthorize("hasAuthority('SCOPE_artisans.pendings')")
    @GetMapping("/artisan/pending")
    public R<List<Artisan>> getPendingArtisans() {
        log.debug("[controller] getPendingArtisans.....");
        List<Artisan> artisans = artisanService.getPendingArtisans();
        return R.ok("success", artisans);
    }

    /**
     * 更新商家
     * @param artisanDTO 商家信息
     *                   仅限商家自己修改，需要 商家 权限
     * @return {@code 200 OK}
     */
    @PreAuthorize("hasAuthority('SCOPE_artisans.update')")
    @PutMapping("/artisan/{id}")
    public Response updateArtisan(@AuthenticationPrincipal Jwt jwt, @PathVariable String id, @RequestBody @Validated(Update.class) ArtisanDTO artisanDTO) {
        //TODO 更新 name/logoUrl 需要更新 所属 Product 的 ArtisanInfo
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        log.debug("[controller] updateArtisan:{}", artisanDTO);
        artisanService.updateArtisan(userId, id, artisanDTO);
        return Response.ok();
    }

    /**
     * 删除商家 需要 管理员 权限
     * @param id 商家id
     * @return {@code 200 OK}
     */
    @PreAuthorize("hasAuthority('SCOPE_artisans.delete')")
    @DeleteMapping("/artisan/{id}")
    public Response deleteArtisan(@PathVariable String id) {
        artisanService.deleteArtisan(id);
        return Response.ok();
    }

}
