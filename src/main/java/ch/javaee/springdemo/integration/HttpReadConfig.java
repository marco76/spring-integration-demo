package ch.javaee.springdemo.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.http.dsl.Http;
import org.springframework.messaging.MessageChannel;

@Configuration
public class HttpReadConfig {

    @MessagingGateway
    public interface HttpRequestGateway {

        @Gateway(requestChannel = "httpOut")
        String request(String request);
    }

    @Bean
    public IntegrationFlow outbound() {

        return IntegrationFlows
                .from("httpOut")
                .handle(
                        Http.outboundGateway("server" + "{service}" + "properties")
                        .httpMethod(HttpMethod.GET)
                        .expectedResponseType(String.class)

                .uriVariable("service", "payload")
                        .extractPayload(true)

                )
                .handle(message -> System.out.println(message.getPayload()))
                .channel("answerChannel")
                .get();
    }

    @Bean
    public QueueChannel errorChannel() {
        QueueChannel channel = new QueueChannel();
        return channel;
    }

    @Bean public
    MessageChannel answerChannel(){
        return new QueueChannel();
    }
}
