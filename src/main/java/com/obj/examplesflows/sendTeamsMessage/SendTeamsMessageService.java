package com.obj.examplesflows.sendTeamsMessage;

import com.obj.nc.domain.content.teams.TeamsMessageContent;
import com.obj.nc.domain.endpoints.TeamsEndpoint;
import com.obj.nc.domain.message.TeamsMessage;
import com.obj.nc.flows.teamsMessageProcessing.TeamsMessageProcessingFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class SendTeamsMessageService {
    @Autowired
    private TeamsMessageProcessingFlow processingFlow;

    void sendMessage(String webhook, String text) {
        TeamsMessage message = new TeamsMessage();

        TeamsEndpoint endpoint = TeamsEndpoint.builder().webhookUrl(webhook).build();
        message.setReceivingEndpoints(Collections.singletonList(endpoint));

        TeamsMessageContent content = TeamsMessageContent.builder().text(text).build();
        message.setBody(content);

        processingFlow.sendMessage(message);
    }
}
