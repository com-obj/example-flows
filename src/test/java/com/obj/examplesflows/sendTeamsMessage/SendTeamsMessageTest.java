package com.obj.examplesflows.sendTeamsMessage;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.obj.nc.testUtils.BaseIntegrationTest;
import com.obj.nc.testUtils.SystemPropertyActiveProfileResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static com.obj.nc.config.PureRestTemplateConfig.PURE_REST_TEMPLATE;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@SpringBootTest
@ActiveProfiles(value = {"test"}, resolver = SystemPropertyActiveProfileResolver.class)
public class SendTeamsMessageTest extends BaseIntegrationTest {
    @Autowired
    SendTeamsMessageService service;

    @Autowired
    @Qualifier(PURE_REST_TEMPLATE)
    RestTemplate restTemplate;

    MockRestServiceServer server;
    static final String TEXT = "Hello World!";
    static final String WEBHOOK_URL = "https://webhook.office.com/webhookb2/b81fce74-a427-4952-bfea-7f78a17657aa@86768cf8-cd8f-4f68-8d6a-b77a895999b0/IncomingWebhook/1b3ce4faf841404e813b2ef207a4ccfb/51165f5a-8c7d-44ff-a6d3-4eef6010ff85";

    @BeforeEach
    void setUp(@Autowired JdbcTemplate jdbcTemplate) {
        purgeNotifTables(jdbcTemplate);
        server = MockRestServiceServer.bindTo(restTemplate).build();

        server.expect(requestTo(WEBHOOK_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(MockRestRequestMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockRestRequestMatchers.content().json("{\"text\":\"" + TEXT + "\"}"))
                .andRespond(MockRestResponseCreators.withSuccess());
    }

    @Test
    void testSendMessage() {
        service.sendMessage(WEBHOOK_URL, TEXT);
        server.verify(Duration.ofSeconds(15));
    }

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("john_doe", "pwd"))
            .withPerMethodLifecycle(false);
}
