package com.obj.examplesflows.genericDataToMessage;

import com.obj.nc.Get;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.annotation.IntegrationComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = {Get.class, GenericDataToNotificationApplication.class})
@IntegrationComponentScan(basePackageClasses = Get.class)
public class GenericDataToNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(GenericDataToNotificationApplication.class, args);
	}

}
