package ch.javaee.springdemo.httpRead;

import ch.javaee.springdemo.integration.HttpReadConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
public class HttpServiceTest {

    @Autowired
    private HttpReadConfig.HttpRequestGateway  gateway;
    MockRestServiceServer server;

    @Before
    public void setup() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void gatewayTest() throws IOException {

        server.expect(requestTo("/rest/service/blabla"))
                .andRespond(withSuccess("{\"result:\":\"cool!\"}", MediaType.APPLICATION_JSON));

        gateway.request("bla");

        server.verify();
    }
}
