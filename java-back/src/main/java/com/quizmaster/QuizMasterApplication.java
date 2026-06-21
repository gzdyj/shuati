package com.quizmaster;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.quizmaster.mapper")
public class QuizMasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizMasterApplication.class, args);
    }
}
