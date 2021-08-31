package com.obj.examplesflows.eventToMessage;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageParser;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.obj.nc.domain.IsTypedJson;
import com.obj.nc.testUtils.SystemPropertyActiveProfileResolver;
import com.obj.nc.utils.JsonUtils;

@SpringBootTest
@ActiveProfiles(value = { "test" }, resolver = SystemPropertyActiveProfileResolver.class)
@AutoConfigureMockMvc
class EventToMessageTests {
		
	@Autowired protected MockMvc mockMvc;

	@Test
	void eventToMessage() throws Exception {
		NewCustomerRegistrationEvent event = NewCustomerRegistrationEvent.builder()
				.customerName("John Doe")
				.customerEmail("john_doe@company.com")
				.build();
        
        ResultActions resp = mockMvc
        		.perform(MockMvcRequestBuilders.post("/events")
        		.content(JsonUtils.writeObjectToJSONString(event))
        		.contentType(APPLICATION_JSON_UTF8)        		
                .accept(APPLICATION_JSON_UTF8));
		
        resp
    		.andExpect(status().is2xxSuccessful())
    		.andExpect(jsonPath("$.ncEventId").value(CoreMatchers.notNullValue()));
        
		boolean recieved = greenMail.waitForIncomingEmail(5000L, 1);
		
		assertEquals(true, recieved);
		assertEquals(1, greenMail.getReceivedMessages().length);
		
	    MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
	    MimeMessageParser parsedEmail = new MimeMessageParser(receivedMessage).parse();
	    
	    assertEquals("We love to have you in the comunity.",parsedEmail.getPlainContent());
	    assertEquals("Welcome on board John Doe",parsedEmail.getSubject());
	    assertEquals(1, receivedMessage.getAllRecipients().length);
	    assertEquals("john_doe@company.com", receivedMessage.getAllRecipients()[0].toString());		
	}

	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	@RegisterExtension
	static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
	  	.withConfiguration(GreenMailConfiguration.aConfig().withUser("john_doe", "pwd"))
	  	.withPerMethodLifecycle(false);

	
}
