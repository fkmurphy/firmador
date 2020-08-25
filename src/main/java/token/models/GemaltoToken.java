package token.models;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.*;
import sample.Main;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.Provider;
import java.security.KeyStore;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.Signature;
import java.security.PrivateKey;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GemaltoToken implements Token {
    private static final long TICKS_POR_DIA = 1000 * 60 * 60 * 24;

    protected String driverPath;
    protected Provider provider;
    public GemaltoToken(){
        this.driverPath = "";
        Provider prototype = Security.getProvider("SunPKCS11");
        this.provider = prototype.configure(getConfig());
        Security.addProvider(provider);
    }
    private KeyStore getKeystoreInstance(String pwd){
        try {
            char[]  password = pwd.toCharArray();
            KeyStore ks = KeyStore.getInstance("PKCS11", this.provider);
            ks.load(null, password);
            return ks;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private X509Certificate getCert(){
        X509Certificate cert = null;
        try {
            KeyStore keystore = getKeystoreInstance(this.getPassword());

            String alias = keystore.aliases().nextElement();
            //get Cert
            cert = (X509Certificate) keystore.getCertificate(alias);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return cert;
    }

    @Override
    public Map<String,String> getInfo() {
        Map<String,String> map = new HashMap<String,String>();
        X509Certificate cert = getCert();
        map.put("issuer",cert.getIssuerDN().toString());
        map.put("not_before",cert.getNotBefore().toString());
        map.put("not_after",cert.getNotAfter().toString());
        map.put("serial",cert.getSerialNumber().toString());
        map.put("version",Integer.toString( cert.getVersion() ));
        map.put("expire",Long.toString(expireCert(cert)));
        return map;
    }

    @Override
    public String getDriverPath() {
        return null;
    }

    @Override
    public Provider getProvider() {
        return this.provider;
    }

    protected String getConfig(){
        return Main.class.getClassLoader().getResource("pkcs11.cfg").getFile();
    }

    private long expireCert(X509Certificate cert)
    {
        long now  = new Date().getTime();
        long to   = cert.getNotAfter().getTime();
        return ((to - now) / TICKS_POR_DIA);
    }

    public Boolean checkValidity(){
        try {
            X509Certificate cert = getCert();
            //Signature s = Signature.getInstance("SHA1withRSA");
            //s.initVerify(keystore.getCertificate(alias));
            cert.checkValidity();
            System.out.println("Validation check passed.");
            return true;
        } catch (CertificateExpiredException e) {
            System.out.println("Certificate expired. Abroting.");
            //System.exit(1);
        } catch (CertificateNotYetValidException e){
            System.out.println("Certificate invalid. Abroting.");
            //System.exit(1);
        }
        return false;
    }

    private static String getPassword() throws IOException {
        String file = Main.class.getClassLoader().getResource("password.txt").getFile();
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        String passwordLine = br.readLine();
        System.out.println(passwordLine);
        return passwordLine;
    }
    public void sign(String src, String dst){
        try {
            String password = this.getPassword();
            KeyStore ks = getKeystoreInstance(password);
            String alias = ks.aliases().nextElement();
            PrivateKey privKey = (PrivateKey) ks.getKey(alias, password.toCharArray());
            Certificate[] chain = ks.getCertificateChain(alias);

            processSign(src, dst, chain,privKey, DigestAlgorithms.SHA256,
                    getProvider().getName(), MakeSignature.CryptoStandard.CMS, "Test 1", "Ghent");
        } catch (IOException | KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

    }

    public void processSign(String src, String dest,
                            Certificate[] chain, PrivateKey pk, String digestAlgorithm, String provider,
                            MakeSignature.CryptoStandard subfilter, String reason, String location)
            throws GeneralSecurityException, IOException, DocumentException {
        // Creating the reader and the stamper
        PdfReader reader = new PdfReader(src);
        FileOutputStream os = new FileOutputStream(dest);
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');
        // Creating the appearance
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        appearance.setVisibleSignature(new Rectangle(36, 748, 144, 780), 1, "sig");
        // Creating the signature
        ExternalDigest digest = new BouncyCastleDigest();
        ExternalSignature signature =
                new PrivateKeySignature(pk, digestAlgorithm, provider);
        MakeSignature.signDetached(appearance, digest, signature, chain,
                null, null, null, 0, subfilter);
    }
}
