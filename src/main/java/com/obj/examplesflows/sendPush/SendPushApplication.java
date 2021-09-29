package com.obj.examplesflows.sendPush;

import com.obj.nc.Get;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.annotation.IntegrationComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = {Get.class, SendPushApplication.class}, basePackages = {"com.obj"})
@IntegrationComponentScan(basePackageClasses = Get.class)
public class SendPushApplication {

	public static void main(String[] args) {
		SpringApplication.run(SendPushApplication.class, args);
	}

}
