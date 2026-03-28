package com.job.assistant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.job.assistant.mapper")
public class PersonalJobAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalJobAssistantApplication.class, args);
    }
}
