package com.cram.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class CustomMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {

        corsRegistry.addMapping("/**")
                .allowedOrigins("*") // 모든 Origin 허용
                .exposedHeaders("Set-Cookie")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // uploads 디렉토리를 절대 경로로 설정
        String uploadPath = Paths.get(System.getProperty("user.dir"), "upload").toAbsolutePath().toUri().toString();

        registry.addResourceHandler("/upload/**")
                .addResourceLocations(uploadPath); // 꼭 file:///... 으로 변환됨
    }
}
