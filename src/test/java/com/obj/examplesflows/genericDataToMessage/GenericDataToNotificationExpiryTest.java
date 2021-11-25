package com.obj.examplesflows.genericDataToMessage;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.obj.nc.testUtils.BaseIntegrationTest;
import com.obj.nc.testUtils.SystemPropertyActiveProfileResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.sql.Timestamp.from;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@ActiveProfiles(value = { "test" }, resolver = SystemPropertyActiveProfileResolver.class)
@SpringBootTest(properties = {
        "nc.data-sources.jdbc[0].name=test-ds",
        "nc.data-sources.jdbc[0].url=jdbc:postgresql://localhost:27305/ds",
        "nc.data-sources.jdbc[0].username=ds",
        "nc.data-sources.jdbc[0].password=ds",
        "nc.data-sources.jdbc[0].jobs[0].name=check-agreements-expiry",
        "nc.data-sources.jdbc[0].jobs[0].entity-name=license_agreement",
        "nc.data-sources.jdbc[0].jobs[0].cron=*/1 * * * * *",
        "nc.data-sources.jdbc[0].jobs[0].expiry-check.field-name=expiry_date",
        "nc.data-sources.jdbc[0].jobs[0].expiry-check.days-until-expiry=5",
        "license-agreements.admin-email=johndoe@objectify.sk",
        "license-agreements.email-template-path=agreements.html",
        "nc.functions.email-templates.templates-root-dir=src/test/resources/templates"
})
class GenericDataToNotificationExpiryTest extends BaseIntegrationTest {
    
    private JdbcTemplate testDbJdbcTemplate;
    
    @BeforeEach
    void setupDbs(@Autowired JdbcTemplate springJdbcTemplate) {
        purgeNotifTables(springJdbcTemplate);
    
        DataSource targetDataSource = DataSourceBuilder
                .create()
                .url("jdbc:postgresql://localhost:27305/ds")
                .username("ds")
                .password("ds")
                .build();
        testDbJdbcTemplate = new JdbcTemplate(targetDataSource);
        persistTestLicenseAgreements(testDbJdbcTemplate);
    }
    
    @AfterEach
    void tearDown() {
        testDbJdbcTemplate.update("delete from license_agreement");
    }
    
    @Test
    void testDataPulledAndMessageSent() {
        // when
        boolean recieved = greenMail.waitForIncomingEmail(5000L, 1);
    
        assertEquals(true, recieved);
        assertEquals(1, greenMail.getReceivedMessages().length);
    
        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
        String body = GreenMailUtil.getBody(receivedMessage);
        assertThat(body).contains("Agreement 1", "Agreement 2", "Agreement 3", "Agreement 4");
    }
    
    private void persistTestLicenseAgreements(JdbcTemplate testDbJdbcTemplate) {
        List<LicenseAgreement> testAgreements = IntStream
                .range(1, 10)
                .mapToObj(i -> 
                        LicenseAgreement
                                .builder()
                                .description("Agreement ".concat(String.valueOf(i)))
                                .expiryDate(Instant.now().plus(i, ChronoUnit.DAYS))
                                .build())
                .collect(Collectors.toList());
    
        testDbJdbcTemplate.batchUpdate(
                "insert into license_agreement (description, expiry_date) values (?, ?) ", 
                new BatchPreparedStatementSetter() {
    
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        LicenseAgreement agreement = testAgreements.get(i);
                        ps.setString(1, agreement.getDescription());
                        ps.setTimestamp(2, from(agreement.getExpiryDate()));
                    }
            
                    public int getBatchSize() {
                        return testAgreements.size();
                    }
    
        });
    }
    
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("john_doe", "pwd"))
            .withPerMethodLifecycle(false);
    
}