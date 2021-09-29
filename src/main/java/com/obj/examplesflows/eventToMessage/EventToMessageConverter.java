package com.obj.examplesflows.eventToMessage;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.obj.nc.domain.content.email.EmailContent;
import com.obj.nc.domain.endpoints.EmailEndpoint;
import com.obj.nc.domain.event.GenericEvent;
import com.obj.nc.domain.message.EmailMessage;
import com.obj.nc.domain.message.Message;
import com.obj.nc.exceptions.PayloadValidationException;
import com.obj.nc.flows.inputEventRouting.extensions.InputEvent2MessageConverterExtension;

@Component
public class EventToMessageConverter implements InputEvent2MessageConverterExtension {

	@Override
	public Optional<PayloadValidationException> canHandle(GenericEvent payload) {
		if (payload.getPayloadAsPojo() instanceof NewCustomerRegistrationEvent) {
			return Optional.empty();
		}

		return Optional.of(new PayloadValidationException("EventToMessageConverter only handles payload of type NewCustomerRegistrationEvent "));
	}

	@Override
	public List<Message<?>> convertEvent(GenericEvent event) {
		NewCustomerRegistrationEvent regEvent = event.getPayloadAsPojo();
		
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
