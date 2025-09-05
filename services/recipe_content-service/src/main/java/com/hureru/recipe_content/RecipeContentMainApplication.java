package com.hureru.recipe_content;

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
public class RecipeContentMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecipeContentMainApplication.class, args);
    }
}
