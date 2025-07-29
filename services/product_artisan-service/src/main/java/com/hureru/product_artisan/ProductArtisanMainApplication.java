package com.hureru.product_artisan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author zheng
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ProductArtisanMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductArtisanMainApplication.class, args);
    }
}
