package com.obj.examplesflows.eventToMessage;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.obj.nc.domain.IsTypedJson;
import com.obj.nc.utils.JsonUtils;

@Component
public class AppInitializer implements ApplicationRunner {

	@Override
    public void run(ApplicationArguments args) throws Exception {
		JsonUtils.getObjectMapper().addMixIn(IsTypedJson.class, NewCustomerRegistrationEvent.class);
    }
}
