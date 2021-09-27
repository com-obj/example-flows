package com.obj.examplesflows.sendPush;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.obj.nc.domain.endpoints.push.PushEndpoint;
import com.obj.nc.domain.message.PushMessage;
import com.obj.nc.functions.processors.deliveryInfo.domain.DeliveryInfo;
import com.obj.nc.functions.processors.senders.PushSender;
import com.obj.nc.repositories.DeliveryInfoRepository;
import com.obj.nc.testUtils.BaseIntegrationTest;
import com.obj.nc.testUtils.SystemPropertyActiveProfileResolver;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.obj.nc.functions.processors.deliveryInfo.domain.DeliveryInfo.DELIVERY_STATUS.SENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

@SpringBootTest
@ActiveProfiles(value = { "test" }, resolver = SystemPropertyActiveProfileResolver.class)
class SendPushTest extends BaseIntegrationTest {
    
    @MockBean private PushSender pushSender;
    @Autowired private SendPushService pushService;
    @Autowired private DeliveryInfoRepository deliveryInfoRepository;
    
    @BeforeEach
    void setUp(@Autowired JdbcTemplate jdbcTemplate) {
        purgeNotifTables(jdbcTemplate);
    }
    
    @Test
    void sendDirectPush() {
        // given
        PushEndpoint endpoint = PushEndpoint.ofToken("test-token");
        
        PushMessage message = pushService
                .createMessage(endpoint, "Subject", "Hello World!");
    
        Mockito
                .when(pushSender.apply(message))
                .thenReturn(message);
    
        // when
        pushService.send(message);
    
        // then
        assertDelivery(endpoint, message);
    }
    
    @Test
    void sendTopicPush() {
        // given
        PushEndpoint endpoint = PushEndpoint.ofTopic("test-topic");
        
        PushMessage message = pushService
                .createMessage(endpoint, "Subject", "Hello World!");
        
        Mockito
                .when(pushSender.apply(message))
                .thenReturn(message);
        
        // when
        pushService.send(message);
        
        // then
        assertDelivery(endpoint, message);
    }
    
    private void assertDelivery(PushEndpoint endpoint, PushMessage message) {
        Awaitility
                .await()
                .atMost(3, TimeUnit.SECONDS)
                .until(() -> deliveryInfoRepository.countByMessageIdAndStatus(message.getId(), SENT) > 0);
        
        List<DeliveryInfo> sentInfos = deliveryInfoRepository.findByMessageIdAndStatus(message.getId(), SENT);
        
        assertThat(sentInfos)
                .hasSize(1)
                .element(0)
                .asInstanceOf(type(DeliveryInfo.class))
                .satisfies(deliveryInfo -> assertThat(deliveryInfo.getEndpointId()).isEqualTo(endpoint.getId()));
    }
    
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("john_doe", "pwd"))
            .withPerMethodLifecycle(false);
    
}