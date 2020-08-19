package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sun.security.pkcs11.SunPKCS11;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


public class Main extends Application {
    private static final String NEW_LINE = "\n";
    private static final String DOUBLE_QUOTE = "\"";
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
        final String name = "ANY_NAME";
        final String library = "";
        final String slot = "1";
        String string = "name = "+name+"\nlibrary = "+library;
        StringBuilder builder = new StringBuilder();

        builder.append("name=SmartCard\n");
        builder.append("showInfo=");
        builder.append("library="+library);
        Provider prototype = Security.getProvider("SunPKCS11");
        Provider provider1 = prototype.configure("/home/jmurphy/pepe.cfg");

        Security.addProvider(provider1);

        System.out.println(provider1.getInfo());
        KeyStore pkcss11KS = null;
        try {
            String file = Main.class.getClassLoader().getResource("password.txt").getFile();
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            String passwordLine = br.readLine();
            System.out.println(passwordLine);
            char[]  password= passwordLine.toCharArray();
            KeyStore keystore = KeyStore.getInstance("PKCS11", provider1);
            keystore.load(null, password);
            String alias = keystore.aliases().nextElement();
            X509Certificate cert = (X509Certificate) keystore.getCertificate(alias);
            System.out.println(cert);

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public static void main(String[] args) {
        algo();
        launch(args);
    }
}
