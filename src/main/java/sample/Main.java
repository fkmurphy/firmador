package sample;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import token.models.GemaltoToken;
import token.models.Token;
import java.io.*;



public class Main extends Application {
    private static final String NEW_LINE = "\n";
    private static final String TOKEN_PIN_STRING = "USTAW_PIN";
    private static final char[] TOKEN_PIN = TOKEN_PIN_STRING.toCharArray();
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
        primaryStage.setTitle("Firmador");
        primaryStage.setScene(new Scene(root, 800, 600));
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.showOpenDialog(primaryStage);
        primaryStage.show();
    }

    public static void algo()
    {
        Token token = new GemaltoToken();
        String src = Main.class.getClassLoader().getResource("uno.pdf").getFile();
        String dest = String.format("/home/jmurphy/hola2.pdf",1);

        token.sign(src, String.format(dest, 1));
    }

    public static void main(String[] args) {
        //algo();
        launch(args);
    }
}
