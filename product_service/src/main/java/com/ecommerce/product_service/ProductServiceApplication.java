package com.ecommerce.product_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import com.ecommerce.security.JwtService;
import com.ecommerce.security.PreFilter;
import com.ecommerce.security.SecurityConfig;

@SpringBootApplication
@Import({SecurityConfig.class, PreFilter.class, JwtService.class})
@EnableFeignClients
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}

}
