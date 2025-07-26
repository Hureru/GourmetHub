package com.hureru.product_artisan.service.Impl;

import com.hureru.product_artisan.bean.Artisan;
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

    @Override
    public Artisan saveArtisan(Artisan artisan) {
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
    public List<Artisan> getArtisansByName(String name) {
        return artisanRepository.findByName(name);
    }

    @Override
    public void deleteArtisan(String id) {
        artisanRepository.deleteById(id);
    }

}
