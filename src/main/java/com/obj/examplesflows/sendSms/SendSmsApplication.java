package com.obj.examplesflows.sendSms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.obj"})
public class SendSmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SendSmsApplication.class, args);
    }
}
