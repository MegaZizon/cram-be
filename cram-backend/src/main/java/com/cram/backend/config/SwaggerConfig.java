package com.cram.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@OpenAPIDefinition(info = @io.swagger.v3.oas.annotations.info.Info(
        title = "SCREEN DOOR API 명세서",
        description = "API.",
        version = "v1"))
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "JWT";

        Server localServer = new Server()
                .url("http://localhost:8080") // 로컬 개발 환경
                .description("Local Server");

        Server ngrokServer = new Server()
                .url("http://175.117.82.139:8080")  // ⭐ 여기에 당신 ngrok 주소
                .description("Local PUBLIC IP SERVER");
        Server ngrokServer2 = new Server()
                .url("http://223.130.146.25:8080")  // ⭐ 여기에 당신 ngrok 주소
                .description("naver");

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(jwtSchemeName))
                .components(new Components().addSecuritySchemes(jwtSchemeName,
                        new SecurityScheme()
                                .name(jwtSchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ))
                .info(new Info()
                        .title("SCREEN DOOR API 명세서")
                        .description("API.").version("v1"))
                        .servers(List.of(localServer    ,ngrokServer,ngrokServer2));  // ✅ 여기에 추가;

    }
}
