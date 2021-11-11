package com.obj.examplesflows.sendSlackMessage;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.obj.nc.config.SlackConfiguration;
import com.obj.nc.testUtils.BaseIntegrationTest;
import com.obj.nc.testUtils.SystemPropertyActiveProfileResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static com.obj.nc.functions.processors.senders.slack.SlackMessageSenderConfig.SLACK_REST_TEMPLATE;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;

@SpringBootTest(properties = {
        "nc.slack.apiUrl=https://slack.com/api",
        "nc.slack.botToken=xoxb-2660284751633-2647758251043-94DXFGpwGyigm6K4mWSxJzX0"
})
@ActiveProfiles(value = {"test"}, resolver = SystemPropertyActiveProfileResolver.class)
public class SendSlackMessageTest extends BaseIntegrationTest {
    @Autowired
    SendSlackMessageService service;

    @Autowired
    @Qualifier(SLACK_REST_TEMPLATE)
    RestTemplate restTemplate;

    @Autowired
    SlackConfiguration config;

    MockRestServiceServer server;
    static final String TEXT = "Hello World!";
    static final String CHANNEL_CODE = "C02K3SFJ0A0";

    @BeforeEach
    void setUp(@Autowired JdbcTemplate jdbcTemplate) {
        purgeNotifTables(jdbcTemplate);

        server = MockRestServiceServer.bindTo(restTemplate).build();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("text", TEXT);
        params.add("channel", CHANNEL_CODE);

        server.expect(requestTo("https://slack.com/api/chat.postMessage"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer " + config.getBotToken()))
                .andExpect(MockRestRequestMatchers.content().formData(params))
                .andRespond(MockRestResponseCreators.withSuccess());
    }

    @Test
    void testSendMessage() {
        service.sendSlackMessage(CHANNEL_CODE, TEXT);
        server.verify(Duration.ofSeconds(15));
    }

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("john_doe", "pwd"))
            .withPerMethodLifecycle(false);
}
