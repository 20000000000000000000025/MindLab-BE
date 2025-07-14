package org.example.mindlab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MindLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(MindLabApplication.class, args);
    }

}
