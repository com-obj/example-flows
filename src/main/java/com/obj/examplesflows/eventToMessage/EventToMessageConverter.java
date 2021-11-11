package com.obj.examplesflows.eventToMessage;

import com.obj.nc.converterExtensions.genericEvent.InputEvent2MessageConverterExtension;
import com.obj.nc.domain.content.email.EmailContent;
import com.obj.nc.domain.endpoints.EmailEndpoint;
import com.obj.nc.domain.event.GenericEvent;
import com.obj.nc.domain.message.EmailMessage;
import com.obj.nc.domain.message.Message;
import com.obj.nc.exceptions.PayloadValidationException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class EventToMessageConverter implements InputEvent2MessageConverterExtension {
	@Override
	public Optional<PayloadValidationException> canHandle(GenericEvent payload) {
		return Optional.empty();
	}

	@Override
	public List<Message<?>> convert(GenericEvent event) {
		NewCustomerRegistrationEvent regEvent = event.getPayloadAsPojo(NewCustomerRegistrationEvent.class);
		
		EmailContent body = EmailContent.builder()
				.subject("Welcome on board " +  regEvent.getCustomerName())
				.text("We love to have you in the comunity.")
				.build();		
		
		EmailEndpoint emailEndpoint = EmailEndpoint.builder().email(regEvent.getCustomerEmail()).build();
		
		EmailMessage msg = new EmailMessage();
		msg.addReceivingEndpoints(emailEndpoint);
		msg.setBody(body);
		
		return Arrays.asList(msg);
	}

}
