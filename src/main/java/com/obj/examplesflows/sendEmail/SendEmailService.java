package com.obj.examplesflows.sendEmail;

import org.springframework.stereotype.Service;

import com.obj.nc.domain.content.email.EmailContent;
import com.obj.nc.domain.endpoints.EmailEndpoint;
import com.obj.nc.domain.message.EmailMessage;
import com.obj.nc.flows.emailFormattingAndSending.EmailProcessingFlow;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SendEmailService {

	private EmailProcessingFlow emailFlow;
	
	public void sendEmail(String recipient, String subject, String msgText) {
		EmailContent body = EmailContent.builder()
				.subject(subject)
				.text(msgText)
				.build();		
		
		EmailEndpoint emailEndpoint = EmailEndpoint.builder().email(recipient).build();
		
		EmailMessage msg = new EmailMessage();
		msg.addRecievingEndpoints(emailEndpoint);
		msg.setBody(body);
		
		emailFlow.sendEmail(msg);		
	}
}
