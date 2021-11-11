package com.obj.examplesflows.sendTeamsMessage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.obj"})
public class SendTeamsMessageApplication {
    public static void main(String[] args) {
        SpringApplication.run(SendTeamsMessageApplication.class, args);
    }
}
