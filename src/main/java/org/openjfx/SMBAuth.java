package org.openjfx;

import jcifs.smb.NtlmAuthenticator;
import jcifs.smb.NtlmPasswordAuthentication;

public class SMBAuth extends NtlmAuthenticator {
    private String username ="jmurphy";
    private String password = "pirumuerte35";
    private String domain = "RNV-075";
    private NtlmAuthenticator ntmlauth;
    public SMBAuth(){
        NtlmAuthenticator.setDefault(this);
    }
}
