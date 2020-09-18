package org.openjfx.file;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import org.openjfx.SMBAuth;

import java.net.MalformedURLException;

public class SambaConnection implements FileRepository{
    private String domain;
    private String username;
    private String password;
    private String server;
    private String path;
    public SambaConnection (String path)
    {
        this.server = "10.114.75.1";
        this.path = path;

        SMBAuth auth = new SMBAuth();
        jcifs.Config.setProperty("jcifs.netbios.wins","10.114.75.1");
        NtlmPasswordAuthentication la = new NtlmPasswordAuthentication(this.domain,this.username,this.password);
        try {
            SmbFile in = new SmbFile("smb://"+server+"/actuacion_test/PDF/"+"231/2020/25.pdf", la);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
    @Override
    public String fileIdentifier ()
    {
        return null;
    }

    @Override
    public String getPath ()
    {
        return this.path;

    }
}
