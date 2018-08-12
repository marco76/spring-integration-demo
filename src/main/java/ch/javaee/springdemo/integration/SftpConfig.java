package ch.javaee.springdemo.integration;

import com.jcraft.jsch.ChannelSftp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

import java.io.File;

@Configuration
public class SftpConfig {

    @Value("sftp.user")
    private String user;
    @Value("sftp.password")
    private String password;
    @Value("sftp.port")
    private Integer port;
    @Value("sftp.host")
    private String host;
    @Value("sftp.remote.directory")
    private String remoteDirectory;

    private static final String INPUT_CHANNEL = "INPUT_CHANNEL";


    @Bean(name = "sftpSessionFactory ")
    public SessionFactory<ChannelSftp.LsEntry> sftpSessionFactory() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
        factory.setHost(host);
        factory.setPort(port);
        factory.setUser(user);
        factory.setPassword(password);

        factory.setAllowUnknownKeys(true);

        return new CachingSessionFactory<ChannelSftp.LsEntry>(factory);
    }

    @Bean
    @ServiceActivator(inputChannel = INPUT_CHANNEL)
    public SftpMessageHandler handler() {

        SftpMessageHandler handler = new SftpMessageHandler(sftpSessionFactory());

        handler.setRemoteDirectoryExpression(new LiteralExpression(remoteDirectory));
        handler.setFileNameGenerator(message -> {
            if (message.getPayload() instanceof File) {
                return ((File)message.getPayload()).getName();
            }
            return null;
        });

        return handler;
    }

    @MessagingGateway
    public interface uploadGateway {

        @Gateway(requestChannel = INPUT_CHANNEL)
        void upload(File file);
    }

}
