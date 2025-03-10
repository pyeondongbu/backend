package com.pyeon.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.origin}")
    private String corsOrigin;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> allowedOrigins = Arrays.asList(corsOrigin.split(","));
        log.info("CORS 허용 오리진: {}", allowedOrigins);
        
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // 모든 오리진 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")  // Authorization 헤더 노출
                .allowCredentials(true)  // 쿠키 및 인증 정보 전송 허용
                .maxAge(3600);  // preflight 캐시 시간 (1시간)
    }
    
}