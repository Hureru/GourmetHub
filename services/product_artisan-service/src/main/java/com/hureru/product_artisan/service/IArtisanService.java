package com.hureru.product_artisan.service;

import com.hureru.product_artisan.bean.Artisan;
import com.hureru.product_artisan.dto.ArtisanDTO;

import java.util.List;
import java.util.Optional;

/**
 * @author zheng
 */
public interface IArtisanService {
    Artisan saveArtisan(Artisan artisan);
    Artisan saveArtisan(ArtisanDTO dto);
    Artisan getArtisanById(String id);
    List<Artisan> getAllArtisans();
    List<Artisan> getPendingArtisans();
    List<Artisan> getArtisansByName(String name);
    void updateArtisan(Long operateId, String id, ArtisanDTO dto);
    void deleteArtisan(String id);
}
