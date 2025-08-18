package com.hureru.product_artisan.service.Impl;

import com.hureru.common.PaginationData;
import com.hureru.common.exception.BusinessException;
import com.hureru.product_artisan.bean.Artisan;
import com.hureru.product_artisan.dto.ArtisanDTO;
import com.hureru.product_artisan.feign.UserFeignClient;
import com.hureru.product_artisan.repository.ArtisanRepository;
import com.hureru.product_artisan.service.IArtisanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zheng
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArtisanServiceImpl implements IArtisanService {
    private final ArtisanRepository artisanRepository;
    private final UserFeignClient userFeignClient;
    private final RocketMQTemplate rocketMQTemplate;

    // 定义 Topic 常量
    public static final String TOPIC_ARTISAN_DELETE = "TOPIC_ARTISAN_DELETE";

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
    @Cacheable(value = "artisans", key = "#id", unless = "#result == null")
    public Artisan getArtisanById(String id, Long operateId) {
        // 判断 是不是商家本人 或者 是否是审核通过的商家
        if (id.equals(operateId.toString()) || userFeignClient.isEffectiveArtisan(id)){
            return artisanRepository.findById(id).orElseThrow(() -> {
                log.error("[严重错误] 存在商家账号，但查询不到商家信息");
                return new BusinessException(404, "找不到ID为 " + id + " 的商家信息");
            });
        }
        throw new BusinessException(404, "商家不存在");
    }

    @Override
    public PaginationData<Artisan> getAllArtisans(int page, int size) {
        // 创建 Pageable 对象，可以添加默认排序
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        // 查询总记录数 (不含分页)
        long total = artisanRepository.count();

        // 执行分页查询
        Page<Artisan> productPage = artisanRepository.findAll(pageable);

        // 转换为自定义的 PaginationData 对象返回
        return new PaginationData<>(
                productPage.getContent(),
                total,
                productPage.getTotalPages(),
                productPage.getNumber(),
                productPage.getSize()
        );
    }

    @Override
    public PaginationData<Artisan> getPendingArtisans(int page, int size) {
        List<String> userIds = userFeignClient.getPendingUsers();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        // 查询总记录数 (不含分页)
        long total = userIds.size();

        // 执行分页查询
        Page<Artisan> productPage = artisanRepository.findByIdIn(userIds, pageable);

        // 转换为自定义的 PaginationData 对象返回
        return new PaginationData<>(
                productPage.getContent(),
                total,
                productPage.getTotalPages(),
                productPage.getNumber(),
                productPage.getSize()
        );
    }

    @Override
    public List<Artisan> getArtisansByName(String name) {
        return artisanRepository.findByName(name);
    }

    @Override
    @CachePut(value = "artisans", key = "#id")
    public void updateArtisan(Long operateId, String id, ArtisanDTO dto) {
        if (!operateId.equals(Long.parseLong(id))){
            throw new BusinessException(403, "无权限操作此数据");
        }
        artisanRepository.save(new Artisan(id, dto.getName(), dto.getBrandStory(), dto.getLocation(), dto.getLogoUrl(), dto.getCertifications()));
    }

    // 使用MQ 异步删除用户
    @Override
    @Transactional
    @CacheEvict(value = "artisans", key = "#id")
    public void deleteArtisan(String id) {
        // 1. 检查商家是否存在，防止发送无效消息
        if (!artisanRepository.existsById(id)) {
            log.warn("尝试删除不存在的商家, ID: {}", id);
            // 可以选择直接返回或抛出异常，这里直接返回
            return;
        }

        // 2. 先删除本地的 Artisan 文档 (MongoDB 操作)
        artisanRepository.deleteById(id);
        log.info("本地商家信息已删除, ID: {}", id);

        // 3. 发送异步消息通知 iam-service 删除用户账号
        //    使用 syncSend 来确保消息成功发送到 Broker，如果发送失败会抛出异常，
        //    得益于 @Transactional 注解，本地的 MongoDB 删除操作也会被回滚。
        try {
            rocketMQTemplate.syncSend(TOPIC_ARTISAN_DELETE, id);
            log.info("成功发送删除用户消息到MQ, User ID: {}", id);
        } catch (Exception e) {
            log.error("发送删除用户消息到MQ失败, User ID: {}. 事务将回滚.", id, e);
            // 抛出运行时异常，触发声明式事务的回滚
            throw new RuntimeException("发送MQ消息失败，回滚商家删除操作", e);
        }
    }

}
