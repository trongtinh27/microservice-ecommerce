package com.ecommerce.shop_service;

import com.ecommerce.security.JwtService;
import com.ecommerce.security.PreFilter;
import com.ecommerce.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@Import({SecurityConfig.class, PreFilter.class, JwtService.class})
@EnableFeignClients
public class ShopServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ShopServiceApplication.class, args);
	}

}
