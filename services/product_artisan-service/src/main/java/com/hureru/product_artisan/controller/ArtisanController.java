package com.hureru.product_artisan.controller;

import com.hureru.iam.dto.group.Update;
import com.hureru.product_artisan.bean.Artisan;
import com.hureru.product_artisan.dto.ArtisanDTO;
import com.hureru.product_artisan.service.IArtisanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/artisan")
    public Artisan addArtisan(@RequestBody @Validated(Update.class) ArtisanDTO artisanDTO) {
        log.info("调用[addArtisan]:{}", artisanDTO);
        return artisanService.saveArtisan(artisanDTO);
    }
}
