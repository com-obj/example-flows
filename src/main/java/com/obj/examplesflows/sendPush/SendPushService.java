package com.obj.examplesflows.sendPush;

import com.obj.nc.domain.content.push.PushContent;
import com.obj.nc.domain.endpoints.push.PushEndpoint;
import com.obj.nc.domain.message.PushMessage;
import com.obj.nc.flows.pushProcessing.PushProcessingFlow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class SendPushService {

	private final PushProcessingFlow pushProcessingFlow;
	
	public PushMessage createMessage(PushEndpoint endpoint, String subject, String msgText) {
		PushMessage message = new PushMessage();
		message.setBody(
				PushContent
						.builder()
						.subject(subject)
						.text(msgText)
						.build()
		);
		message.setReceivingEndpoints(
				Collections.singletonList(endpoint)
		);
		return message;		
	}
	
	public void send(PushMessage message) {
		pushProcessingFlow.sendPushMessage(message);
	}
	
}
