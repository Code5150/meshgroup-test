package com.code5150.meshgrouptest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;

// TODO: когда буду делать кэш, убрать exclude Redis
@SpringBootApplication(exclude = {DataRedisAutoConfiguration.class})
public class MeshgroupTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(MeshgroupTestApplication.class, args);
    }
}
