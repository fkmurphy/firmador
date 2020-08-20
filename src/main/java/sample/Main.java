package sample;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.security.Provider;
import java.security.KeyStore;
import java.security.UnrecoverableKeyException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.Signature;
import java.security.Security;
import java.security.PrivateKey;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;


public class Main extends Application {
    private static final String NEW_LINE = "\n";
    private static final long TICKS_POR_DIA = 1000 * 60 * 60 * 24;

    private static final String SUN_PKCS11_PROVIDERNAME = "SunPKCS11";
    private static final String TOKEN_PIN_STRING = "USTAW_PIN";
    private static final char[] TOKEN_PIN = TOKEN_PIN_STRING.toCharArray();
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }
    public static void algo() {
        /*final String name = "ANY_NAME";
        final String library = "";
        final String slot = "1";
        StringBuilder builder = new StringBuilder();

        builder.append("name=SmartCard\n");
        builder.append("showInfo=");
        builder.append("library="+library);
         */
        Provider prototype = Security.getProvider("SunPKCS11");
        Provider provider1 = prototype.configure(getConfig());
        Security.addProvider(provider1);

        System.out.println(provider1.getInfo());

        KeyStore pkcss11KS = null;
        try {
            char[]  password = getPassword().toCharArray();
            KeyStore keystore = KeyStore.getInstance("PKCS11", provider1);
            keystore.load(null, password);
            String alias = keystore.aliases().nextElement();
            //get Cert
            X509Certificate cert = (X509Certificate) keystore.getCertificate(alias);
            System.out.println("issuer     " + cert.getIssuerDN());
            System.out.println("not before " + cert.getNotBefore());
            System.out.println("not after  " + cert.getNotAfter());
            System.out.println("serial     " + cert.getSerialNumber());
            System.out.println("version    " + cert.getVersion());

            dateCerts(cert);

            try {
                Signature s = Signature.getInstance("SHA1withRSA");
                s.initVerify(keystore.getCertificate(alias));
                cert.checkValidity();
                System.out.println("Validation check passed.");
            } catch (Exception e) {
                System.out.println("Certificate expired or invalid. Abroting.");
                System.exit(1);
            }
            String src = Main.class.getClassLoader().getResource("uno.pdf").getFile();
            String dest = "/home/jmurphy/hola.pdf";

            PrivateKey privKey = (PrivateKey) keystore.getKey(alias, password);
            Certificate[] chain = keystore.getCertificateChain(alias);

            sign(src, String.format(dest, 1), chain,privKey, DigestAlgorithms.SHA256,
                    provider1.getName(), MakeSignature.CryptoStandard.CMS, "Test 1", "Ghent");

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            //read password from file
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }


    }
    public static void sign(String src, String dest,
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
    private static void dateCerts(X509Certificate cert) {

        long now  = new Date().getTime();
        long to   = cert.getNotAfter().getTime();

        System.out.println("Cert expire " +  ( to - now ) / TICKS_POR_DIA + " days");

    }

    public static String getConfig(){
        return Main.class.getClassLoader().getResource("pkcs11.cfg").getFile();
    }
    private static String getPassword() throws IOException {
        String file = Main.class.getClassLoader().getResource("password.txt").getFile();
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        String passwordLine = br.readLine();
        System.out.println(passwordLine);
        return passwordLine;
    }
    public static void main(String[] args) {
        algo();
        launch(args);
    }
}
