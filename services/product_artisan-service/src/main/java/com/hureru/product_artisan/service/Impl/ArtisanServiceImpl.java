package com.hureru.product_artisan.service.Impl;

import com.hureru.common.exception.BusinessException;
import com.hureru.product_artisan.bean.Artisan;
import com.hureru.product_artisan.dto.ArtisanDTO;
import com.hureru.product_artisan.feign.UserFeignClient;
import com.hureru.product_artisan.repository.ArtisanRepository;
import com.hureru.product_artisan.service.IArtisanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author zheng
 */
@Service
@RequiredArgsConstructor
public class ArtisanServiceImpl implements IArtisanService {
    private final ArtisanRepository artisanRepository;
    private final UserFeignClient userFeignClient;

    @Override
    public Artisan saveArtisan(Artisan artisan) {
        return artisanRepository.save(artisan);
    }

    @Override
    public Artisan saveArtisan(ArtisanDTO dto) {
        Artisan artisan = new Artisan(dto.getId(),
                dto.getName(),dto.getBrandStory(),
                dto.getLocation(), dto.getLogoUrl(),
                dto.getCertifications());
        artisan.setCreatedAt(java.time.LocalDateTime.now().toString());
        artisan.setUpdatedAt(java.time.LocalDateTime.now().toString());
        return artisanRepository.save(artisan);
    }

    @Override
    public Optional<Artisan> getArtisanById(String id) {
        return artisanRepository.findById(id);
    }

    @Override
    public List<Artisan> getAllArtisans() {
        return artisanRepository.findAll();
    }

    @Override
    public List<Artisan> getPendingArtisans() {
        List<String> userIds = userFeignClient.getPendingUsers();
        return artisanRepository.findByIdIn(userIds);
    }

    @Override
    public List<Artisan> getArtisansByName(String name) {
        return artisanRepository.findByName(name);
    }

    @Override
    public void updateArtisan(Long operateId, String id, ArtisanDTO dto) {
        if (!operateId.equals(Long.parseLong(id))){
            throw new BusinessException(403, "无权限操作此数据");
        }
        artisanRepository.save(new Artisan(id, dto.getName(), dto.getBrandStory(), dto.getLocation(), dto.getLogoUrl(), dto.getCertifications()));
    }

    //TODO 使用MQ 异步删除用户
    @Override
    public void deleteArtisan(String id) {
        // 删除用户
        userFeignClient.deleteUser(id);
        artisanRepository.deleteById(id);
    }

}
