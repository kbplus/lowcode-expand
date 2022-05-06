package com.kbplus.demo.ext;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.rdpass.dynamic.demo.mapper")
@SpringBootApplication
public class Run3Application {
    public static void main(String[] args) {
        SpringApplication.run(Run3Application.class);
    }
}
