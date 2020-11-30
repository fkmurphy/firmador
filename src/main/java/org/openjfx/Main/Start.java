package org.openjfx.Main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.openjfx.token.models.ConfigureProvider;
import org.openjfx.token.models.LocalProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.update4j.Configuration;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.util.*;
import java.util.regex.Pattern;


public class Start extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        /**
         * update
         */


            //System.out.println(getClass().getResource("/org/openjfx/Main/scene.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scene.fxml"));
        Map<String,String> map = null;
        try {
            map = processArgs(getParameters().getRaw());
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setTitle("Atención");
            alert.setContentText("No se han podido obtener archivos externos.");

            alert.showAndWait();
        }

        Parent root = (Parent) loader.load();
        FXMLController controller = loader.getController();
        controller.setBackendMap(map);
        controller.setStage(stage);
        controller.setGetHostController(getHostServices());

        /*Alert a = new Alert(Alert.AlertType.ERROR);
        EventHandler<ActionEvent> event = new
                EventHandler<ActionEvent>() {
                    public void handle(ActionEvent e)
                    {
                        // set alert type
                        a.setAlertType(Alert.AlertType.CONFIRMATION);

                        // show the dialog
                        a.show();
                    }
                };
        */
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.setTitle("Firmador");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        /*System.out.println("BootApplication#main");

        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                try {
                    start();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 5000, 60000);
        */
        launch(args);
    }

    public static void start() throws Exception {
        /* AutoUpdate TODO
        System.out.println(System.currentTimeMillis());
        URL configUrl = new URL("http://127.0.0.1:8585/config.xml?" + System.currentTimeMillis());
        Configuration config = null;
        try (Reader in = new InputStreamReader(configUrl.openStream(), StandardCharsets.UTF_8)) {
            config = Configuration.read(in);
        } catch (IOException e) {
            System.err.println("Could not load remote config, falling back to local.");
            try (Reader in = Files.newBufferedReader(Path.of(Start.class.getResource("config.xml").getPath()))) {
                config = Configuration.read(in);
            }
        }

        StartupProgram startup = new StartupProgram(config);

        startup.launch();*/
    }



    private static Map<String,String> processArgs (List<String> args) {
        Map<String,String> map = new HashMap<>();
        boolean exist = false;
        String arg = "";

        final String regexParameters = "\\?";
        final Pattern regexIP = Pattern.compile("((?:(?:https?|ftp):\\/\\/){0,1})" +
                "((((?!-)[A-Za-z0–9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6})" +
                "|" +
                "((([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5]))))" +
                "((\\:[0-9]{1,4}){0,1})" +
                "(\\/*).*");
        String[] parameters = new String[0];

        ListIterator<String> iterator = args.listIterator();

        while (!exist && iterator.hasNext()) {

            arg = iterator.next();
            if (arg.contains("firmador://")) {
                arg = arg.replaceFirst("firmador://","");
                parameters = arg.split(regexParameters);
                exist = true;
            }
        }
        if(exist){
            String key, value= "";
            String[] processParam;
            for (String parameter : parameters) {
                if (regexIP.matcher(parameter).matches()){
                    map.put("api_url",parameter);

                } else {
                    processParam = parameter.split("=");
                    map.put(processParam[0].replaceAll("=",""),processParam[1]);
                }
            }
        }

        return map;
    }

}
