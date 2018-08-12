package ch.javaee.springdemo.sftp;

import ch.javaee.springdemo.integration.SftpConfig;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class SftpServiceImplTest {

    private static Path sftpFolder;

    SshServer server;

    @Autowired
    SftpConfig.uploadGateway uploadGateway;

    @Before
    public void serverSetup() throws IOException {

        server = SshServer.setUpDefaultServer();
        server.setPasswordAuthenticator((arg0, arg1,arg2) -> true);

        server.setPort(12345);
        server.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File("hostkey.ser")));
        server.setSubsystemFactories(Collections.singletonList(
                new SftpSubsystemFactory()));

        final String pathname= System.getProperty("java.io.tmpdir") + File.separator + "sftptestt" + File.separator;
        new File(pathname).mkdirs();
        sftpFolder
                = Paths.get(pathname);

        server.setFileSystemFactory(new VirtualFileSystemFactory(Paths.get(pathname)));


        server.start();

    }

    @Test
    public void testGateway() throws IOException {

        Path tempfile = Files.createTempFile("UPLOAD_TEST", ".doc");
        assertEquals(0, Files.list(sftpFolder).count());

        uploadGateway.upload(tempfile.toFile());

        List<Path> paths = Files.list(sftpFolder).collect(Collectors.toList());
        assertEquals(1, paths.size());
        assertEquals(tempfile.getFileName(), paths.get(0).getFileName());
    }
}
