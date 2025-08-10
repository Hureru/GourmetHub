package com.hureru.product_artisan.service.Impl;

import com.hureru.common.exception.BusinessException;
import com.hureru.product_artisan.bean.Artisan;
import com.hureru.product_artisan.dto.ArtisanDTO;
import com.hureru.product_artisan.feign.UserFeignClient;
import com.hureru.product_artisan.repository.ArtisanRepository;
import com.hureru.product_artisan.service.IArtisanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

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
    private final RabbitTemplate rabbitTemplate;

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

    // 使用MQ 异步删除用户
    @Override
    public void deleteArtisan(String id) {
        // 1. 先删除本地的 Artisan 文档
        artisanRepository.deleteById(id);
        log.info("本地商家信息已删除, ID: {}", id);

        // 2. 发送异步消息通知 iam-service 删除用户
        // 定义交换机和路由键，最好使用常量
        final String EXCHANGE_NAME = "gourmethub.direct";
        final String ROUTING_KEY = "routing.user.delete";

        try {
            // 使用 convertAndSend 发送消息，Spring 会自动处理序列化
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, id);
            log.info("成功发送删除用户消息到MQ, User ID: {}", id);
        } catch (Exception e) {
            // 异常处理：例如记录日志，或者启动一个补偿任务
            log.error("发送删除用户消息到MQ失败, User ID: {}. 错误: {}", id, e.getMessage());
            // 这里可以抛出异常或进行其他补偿逻辑
            //TODO 写入失败日志
            throw new BusinessException(500, "发送删除用户消息到MQ失败");
        }
    }

}
