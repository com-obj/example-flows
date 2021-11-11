package com.obj.examplesflows.sendSms;

import com.obj.nc.domain.content.sms.SimpleTextContent;
import com.obj.nc.domain.endpoints.SmsEndpoint;
import com.obj.nc.domain.message.SmsMessage;
import com.obj.nc.flows.smsFormattingAndSending.SmsProcessingFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class SendSmsService {
    @Autowired
    private SmsProcessingFlow processingFlow;

    void sendMessage(String phoneNumber, String text) {
        SmsMessage message = new SmsMessage();

        SmsEndpoint endpoint = SmsEndpoint.builder().phone(phoneNumber).build();
        message.setReceivingEndpoints(Collections.singletonList(endpoint));

        SimpleTextContent content = SimpleTextContent.builder().text(text).build();
        message.setBody(content);

        processingFlow.sendMessage(message);
    }
}
