package ch.javaee.springdemo.mail;

import ch.javaee.springdemo.integration.IntegrationConfig;
import ch.javaee.springdemo.integration.MailConfig;
import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import com.sun.xml.internal.ws.client.SenderException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {IntegrationConfig.class, MailConfig.class})
public class EmailServiceImplTest {

    @Autowired
    MailConfig.MailGateway mailGateway;

    @Test
    public void mailsend() throws IOException {

        SimpleSmtpServer mailServer = SimpleSmtpServer.start(12345);

        mailGateway.sendMail(buildMailMessage("Test 1", "content 1"));
        mailGateway.sendMail(buildMailMessage("Test 2", "content 2"));

        mailServer.stop();

        List<SmtpMessage> messagesSent = mailServer.getReceivedEmails();

        assertEquals(2, messagesSent.size());
    }


    @Test(expected = Exception.class)
    public void sendMailServerNotAnswerting() throws Exception {

        mailGateway.sendMail(buildMailMessage("Fail test", "empty"));

    }

    private MailMessage buildMailMessage(String subject, String content){

        MailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("marco.molteni@javaee.ch");
        mailMessage.setFrom("marco@javaee.ch");
        mailMessage.setText(content);
        mailMessage.setSubject(subject);

        return mailMessage;
    }



}
