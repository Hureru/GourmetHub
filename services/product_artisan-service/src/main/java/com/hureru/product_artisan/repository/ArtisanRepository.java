package com.hureru.product_artisan.repository;

import com.hureru.product_artisan.bean.Artisan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zheng
 */
@Repository
public interface ArtisanRepository  extends MongoRepository<Artisan, String> {

    List<Artisan> findByName(String name);
}
