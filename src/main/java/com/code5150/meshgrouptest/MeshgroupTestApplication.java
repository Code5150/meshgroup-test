package com.code5150.meshgrouptest;

import com.code5150.meshgrouptest.config.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@OpenAPIDefinition(info = @Info(
        title = "Money transfer application",
        version = "1.0"
))
@SecurityScheme(
        name = SecuritySchemes.BEARER_AUTH,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class MeshgroupTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(MeshgroupTestApplication.class, args);
    }
}
