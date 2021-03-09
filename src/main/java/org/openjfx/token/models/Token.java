package org.openjfx.token.models;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Map;

public interface Token {
    public Map<String,String> getInfo() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException;
    public String getDriverPath();
    public Provider getProvider();
    public void sign(String src, String dst) throws GeneralSecurityException, DocumentException, IOException;
    public void signWithPositionStamper(String src, String dst, int posX, int posY) throws GeneralSecurityException, DocumentException, IOException;
}
