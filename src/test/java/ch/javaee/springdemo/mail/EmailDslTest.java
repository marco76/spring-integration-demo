package ch.javaee.springdemo.mail;

import ch.javaee.springdemo.integration.IntegrationConfig;
import ch.javaee.springdemo.integration.MailDslConfig;
import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {IntegrationConfig.class, MailDslConfig.class})
public class EmailDslTest {

    @Autowired
    MessageChannel smtpFlowChannel;

    @Test
    public void mailsend() throws IOException {

        SimpleSmtpServer mailServer = SimpleSmtpServer.start(12345);
        smtpFlowChannel.send(new GenericMessage<>(buildMailMessage("Test 2", "content 1")));
        smtpFlowChannel.send(new GenericMessage<>(buildMailMessage("Test 3", "content 2")));
        smtpFlowChannel.send(new GenericMessage<>(buildMailMessage("Test 4", "content 2")));

        mailServer.stop();

        List<SmtpMessage> messagesSent = mailServer.getReceivedEmails();

        assertEquals(3, messagesSent.size());
    }

    @Test(expected = Exception.class)
    public void sendMailServerNotAnswerting() throws Exception {

        smtpFlowChannel.send(new GenericMessage<>(buildMailMessage("Test 1", "content 1")));

    }

    private MailMessage buildMailMessage(String subject, String content){

        MailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("marco.molteni@jdsl.ch");
        mailMessage.setFrom("marco@jdsl.ch");
        mailMessage.setText(content);
        mailMessage.setSubject(subject);

        return mailMessage;
    }

}
