package com.obj.examplesflows.sendSlackMessage;

import com.obj.nc.domain.content.slack.SlackMessageContent;
import com.obj.nc.domain.endpoints.SlackEndpoint;
import com.obj.nc.domain.message.SlackMessage;
import com.obj.nc.flows.slackMessageProcessingFlow.SlackMessageProcessingFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class SendSlackMessageService {
    @Autowired
    private SlackMessageProcessingFlow processingFlow;

    void sendSlackMessage(String channel, String text) {
        SlackMessage message = new SlackMessage();

        SlackMessageContent content = SlackMessageContent.builder().text(text).build();
        message.setBody(content);

        SlackEndpoint endpoint = SlackEndpoint.builder().channel(channel).build();
        message.setReceivingEndpoints(Collections.singletonList(endpoint));

        processingFlow.sendMessage(message);
    }
}
