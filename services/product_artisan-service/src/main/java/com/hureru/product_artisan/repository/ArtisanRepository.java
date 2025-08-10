package com.hureru.product_artisan.repository;

import com.hureru.product_artisan.bean.Artisan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zheng
 */
@Repository
public interface ArtisanRepository  extends MongoRepository<Artisan, String> {

    List<Artisan> findByName(String name);
    // 根据ID列表获取 商家列表
    Page<Artisan> findByIdIn(List<String> ids, Pageable pageable);

}
