package org.openjfx.token.models;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.*;
import org.openjfx.HelloFX;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;
import java.time.LocalDateTime;
import java.util.*;

public class GemaltoToken implements Token {
    private static final long TICKS_POR_DIA = 1000 * 60 * 60 * 24;
    protected ExternalSignature signature =null;
    protected String driverPath;
    private char[] pwd;
    protected Provider provider;
    public GemaltoToken(String pwd){
        this.driverPath = "";
        Provider prototype = Security.getProvider("SunPKCS11");
        this.provider = prototype.configure("--name=eToken\n" +
                "library=/lib64/libeToken.so\n" +
                "slot=0");
        this.pwd = pwd.toCharArray();
        Security.addProvider(provider);
    }
    private KeyStore getKeystoreInstance(){
        try {
            KeyStore ks = KeyStore.getInstance("PKCS11", this.provider);
            ks.load(null, pwd);
            return ks;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Una excepción con el token");
            //e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Other exception");
        }
        return null;
    }

    private X509Certificate getCert(){
        X509Certificate cert = null;
        try {
            KeyStore ks = getKeystoreInstance();
            if(ks == null)
                throw new NullPointerException();
            Enumeration<String> aliases = ks.aliases();
            if(aliases != null){
                String alias = aliases.nextElement();
                cert = (X509Certificate) ks.getCertificate(alias);
            }
            // TODO: 6/10/20 exception if null
            //get Cert

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NullPointerException p) {
            System.out.println("null");
            p.printStackTrace();
        }
        return cert;
    }

    @Override
    public Map<String,String> getInfo() {
        Map<String,String> map = new HashMap<>();
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
        return HelloFX.class.getResource("pkcs11.cfg").getFile();
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

    /*
    private static String getPassword() throws IOException {
        Servitring file = Main.class.getClassLoader().getResource("password.txt").getFile();
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        String passwordLine = br.readLine();
        System.out.println(passwordLine);
        return passwordLine;
    }*/
    public void sign(String src, String dst){
        try {
            KeyStore ks = getKeystoreInstance();
            if(ks == null)
                throw new NullPointerException();
            Enumeration<String> aliases = ks.aliases();
            PrivateKey privKey = null;
            Certificate[] chain = null;
            if (aliases != null){
                String alias = aliases.nextElement();
                privKey = (PrivateKey) ks.getKey(alias, pwd);
                chain = ks.getCertificateChain(alias);
            }

            if(signature == null && privKey != null) {
                signature =
                        new PrivateKeySignature(privKey, DigestAlgorithms.SHA256, ks.getProvider().getName());
            }

            // TODO: 6/10/20 excepcion por null
            processSign(src, dst, chain,privKey, DigestAlgorithms.SHA256,
                    getProvider().getName(), MakeSignature.CryptoStandard.CMS,
                    "-", "Viedma, Río Negro, Argentina");
        } catch (IOException e) {
            e.printStackTrace();
        }catch (KeyStoreException e) {
            System.out.println("Error keystore");
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            System.out.println("Error UnrecoverableKey");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error nosuch algorith");
            e.printStackTrace();
        } catch (DocumentException e) {
            System.out.println("Error documentexceptin");
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            System.out.println("Error general security");
            e.printStackTrace();
        }

    }

    public void processSign(String src, String dest,
                            Certificate[] chain, PrivateKey pk, String digestAlgorithm, String provider,
                            MakeSignature.CryptoStandard subfilter, String reason, String location)
            throws GeneralSecurityException, IOException, DocumentException {

        // Creating the reader and the stamper
        PdfReader reader = new PdfReader(src);
        Rectangle lala = reader.getPageSize(reader.getNumberOfPages());
        System.out.println("size"+lala.toString()+"bottom"+lala.getBottom()+"borderwidh:"+lala.getBorderWidth());
        FileOutputStream os = new FileOutputStream(dest);
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0',null,true);
        // Creating the appearance
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        //permitir firmado
        appearance.setCertificationLevel(PdfSignatureAppearance.CERTIFIED_FORM_FILLING);

        System.out.println(LocalDateTime.now().toString());
        int lastPage = reader.getNumberOfPages();
        // 40 = 1cm
        // 40 init x | 40 init y (1cmX x 1cmY)
        // 40+120 | 40 + 40
        appearance.setVisibleSignature(new Rectangle(40, 40, 40+120, 40+40), lastPage, "sig"+ (new Random()).nextInt(25));
        // Creating the signature
        ExternalDigest digest = new BouncyCastleDigest();

        MakeSignature.signDetached(appearance, digest, signature, chain,
                null, null, null, 0, subfilter);
        stamper.close();
        os.close();
        reader.close();
    }
}
