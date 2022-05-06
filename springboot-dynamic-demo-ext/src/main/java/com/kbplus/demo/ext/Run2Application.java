package com.rdpaas.demo.ext;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.rdpass.dynamic.demo.mapper")
@SpringBootApplication
public class Run2Application {
    public static void main(String[] args) {
        SpringApplication.run(Run2Application.class);
    }
}
