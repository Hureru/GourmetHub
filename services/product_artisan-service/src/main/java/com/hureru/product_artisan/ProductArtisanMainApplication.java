package com.hureru.product_artisan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author zheng
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class ProductArtisanMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductArtisanMainApplication.class, args);
    }
}
