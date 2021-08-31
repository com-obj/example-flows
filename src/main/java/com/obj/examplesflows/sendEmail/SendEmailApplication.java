package com.obj.examplesflows.sendEmail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.annotation.IntegrationComponentScan;

import com.obj.nc.Get;

@SpringBootApplication
@ComponentScan(basePackageClasses = {Get.class, SendEmailApplication.class})
@IntegrationComponentScan(basePackageClasses = Get.class)
public class SendEmailApplication {

	public static void main(String[] args) {
		SpringApplication.run(SendEmailApplication.class, args);
	}

}
