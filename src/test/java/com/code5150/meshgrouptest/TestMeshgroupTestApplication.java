package com.code5150.meshgrouptest;

import org.springframework.boot.SpringApplication;

public class TestMeshgroupTestApplication {

    public static void main(String[] args) {
        SpringApplication.from(MeshgroupTestApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
