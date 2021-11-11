package com.obj.examplesflows.sendSlackMessage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.obj"})
public class SendSlackMessageApplication {
    public static void main(String[] args) {
        SpringApplication.run(SendSlackMessageApplication.class, args);
    }
}
