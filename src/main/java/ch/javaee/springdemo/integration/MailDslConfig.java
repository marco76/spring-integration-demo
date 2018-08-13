package ch.javaee.springdemo.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.mail.MailSendingMessageHandler;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource("classpath:/smtp.properties")
public class MailDslConfig {

    @Value("${smtp.host}")
    private String smptHost;

    @Value("${smtp.port}")
    private Integer smtpPort;

    @Bean
    public JavaMailSenderImpl mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(smptHost);
        mailSender.setPort(smtpPort);

        return mailSender;
    }


    @Bean
    public IntegrationFlow smtpFlow() {
        return IntegrationFlows.from("smtpFlowChannel")
                .handle(new MailSendingMessageHandler(mailSender()))
                .get();
    }
}
