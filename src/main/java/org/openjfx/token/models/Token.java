package org.openjfx.token.models;

import org.openjfx.Main.file.exceptions.BadPasswordTokenException;

import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.util.Map;

public interface Token {
    public Map<String,String> getInfo() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, BadPasswordTokenException;
    public String getDriverPath();
    public Provider getProvider();
    public void sign(String src, String dst) throws GeneralSecurityException, BadPasswordTokenException;
    public void signWithPositionStamper(String src, String dst, int posX, int posY) throws GeneralSecurityException, BadPasswordTokenException;
    public void signWithPositionStamper(String src, String dst, int posX, int posY, String stampImage, String stampReason) throws GeneralSecurityException, BadPasswordTokenException;

}
