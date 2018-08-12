package ch.javaee.springdemo.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.NullChannel;
import org.springframework.integration.mail.MailSendingMessageHandler;
import org.springframework.mail.MailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MailConfig {


    @Bean
    public JavaMailSenderImpl mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("localhost");
        mailSender.setPort(12345);

        return mailSender;
    }

    @MessagingGateway
    public interface MailGateway {

        @Gateway(requestChannel = "outboundEmailChannel")
        void sendMail(MailMessage mailMessage);

    }

    @Bean public MessageChannel outboundEmailChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel replyEmailChannel() {
        return new NullChannel();
    }

    @ServiceActivator(inputChannel = "outboundEmailChannel", outputChannel = "replyEmailChannel")
    public MessageHandler mailsSenderMessagingHandler (Message<MailMessage> message) {

        MailSendingMessageHandler mailSendingMessageHandler = new MailSendingMessageHandler(mailSender());
        mailSendingMessageHandler.handleMessage(message);

        return mailSendingMessageHandler;
    }
}
