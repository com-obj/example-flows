package com.obj.examplesflows.eventToMessage;

import com.obj.nc.Get;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.annotation.IntegrationComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = {Get.class, EventToMessageApplication.class})
@IntegrationComponentScan(basePackageClasses = Get.class)
public class EventToMessageApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventToMessageApplication.class, args);
	}

}
