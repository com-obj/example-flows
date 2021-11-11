package com.obj.examplesflows.sendSms;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.obj.nc.functions.processors.senders.sms.GatewayApiConfig;
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

import static com.obj.nc.config.PureRestTemplateConfig.PURE_REST_TEMPLATE;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;

@SpringBootTest(properties = {
        "nc.sms.gateway-api.sendSmsUrl=https://gatewayapi.com/rest/mtsms",
        "nc.sms.gateway-api.token=BwVlR-mcSTScS57syYbkPzyHOVfA7M3AGpLh-kDHO6O9s6WVvXg-zC2qFPPhtSSL",
        "nc.sms.gateway-api.sender=Objectify"
})
@ActiveProfiles(value = {"test"}, resolver = SystemPropertyActiveProfileResolver.class)
public class SendSmsTest extends BaseIntegrationTest {
    @Autowired
    SendSmsService service;

    @Autowired
    @Qualifier(PURE_REST_TEMPLATE)
    RestTemplate restTemplate;

    @Autowired
    GatewayApiConfig config;

    MockRestServiceServer server;
    static final String TEXT = "Hello World!";
    static final String PHONE_NUMBER = "+421950430659";

    @BeforeEach
    void setUp(@Autowired JdbcTemplate jdbcTemplate) {
        purgeNotifTables(jdbcTemplate);

        server = MockRestServiceServer.bindTo(restTemplate).build();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("message", TEXT);
        params.add("sender", config.getSender());
        params.add("token", config.getToken());
        params.add("recipients.0.msisdn", PHONE_NUMBER);

        server.expect(requestTo("https://gatewayapi.com/rest/mtsms"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(MockRestRequestMatchers.content().formData(params))
                .andRespond(MockRestResponseCreators.withSuccess());
    }

    @Test
    void testSendMessage() {
        service.sendMessage(PHONE_NUMBER, TEXT);
        server.verify(Duration.ofSeconds(15));
    }

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("john_doe", "pwd"))
            .withPerMethodLifecycle(false);
}
