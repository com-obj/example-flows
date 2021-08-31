package com.obj.examplesflows.sendEmail;

import static org.junit.Assert.assertEquals;

import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.obj.nc.testUtils.SystemPropertyActiveProfileResolver;

@SpringBootTest
@ActiveProfiles(value = { "test" }, resolver = SystemPropertyActiveProfileResolver.class)
class SendEmailTests {
	
	@Autowired private SendEmailService emailService;	

	@RegisterExtension
	static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
	  	.withConfiguration(GreenMailConfiguration.aConfig().withUser("john_doe", "pwd"))
	  	.withPerMethodLifecycle(false);

	@Test
	void sendSimpleEmail() throws Exception {
		emailService.sendEmail("john_doe@company.com", "subject", "Hello World!");
		
		boolean recieved = greenMail.waitForIncomingEmail(5000L, 1);
		
		assertEquals(true, recieved);
		assertEquals(1, greenMail.getReceivedMessages().length);
		
	    MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
	    
	    assertEquals("Hello World!",new MimeMessageParser(receivedMessage).parse().getPlainContent());
	    assertEquals(1, receivedMessage.getAllRecipients().length);
	    assertEquals("john_doe@company.com", receivedMessage.getAllRecipients()[0].toString());
		
	}

}
