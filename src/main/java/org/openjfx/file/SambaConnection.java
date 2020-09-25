package org.openjfx.file;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import org.openjfx.SMBAuth;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class SambaConnection implements FileRepository{

    private String server;
    private NtlmPasswordAuthentication smbConnect;

    public SambaConnection ()
    {
        this.server = "x.x.x.x";
        String domain = "xxx-075";
        String username = "xxxxx";
        String password = "xxxxx";


        SMBAuth auth = new SMBAuth();
        jcifs.Config.setProperty("jcifs.netbios.wins","x.x.x.x");
        this.smbConnect = new NtlmPasswordAuthentication(domain,username,password);
    }

    @Override
    public String getPath ()
    {
        String dstPath = null;
        try {
            SmbFile in = new SmbFile("smb://"+this.server+"/actuacion_test/PDF/"+"231/2020/51.pdf", smbConnect);
            String fileName = "231_2020_51.pdf";
            dstPath = System.getProperty("java.io.tmpdir") + "/" + fileName;
            CopyOption[] options = {REPLACE_EXISTING};
            Files.copy(in.getInputStream(), Path.of(dstPath), options );
            in.getInputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dstPath;

    }

    @Override
    public String representativeName() {
        return getPath();
    }
}
