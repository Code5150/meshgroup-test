package com.code5150.meshgrouptest;

import com.code5150.meshgrouptest.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;

// TODO: когда буду делать кэш, убрать exclude Redis
@SpringBootApplication(exclude = {DataRedisAutoConfiguration.class})
@OpenAPIDefinition(info = @Info(
        title = "Приложение по переводу денег между счетами",
        version = "1.0",
        description = "Документация для микросервиса"
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
