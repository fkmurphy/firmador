package org.openjfx;

import com.itextpdf.text.ListItem;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Pattern;


public class HelloFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scene.fxml"));
        Map<String,String> map = processArgs(getParameters().getRaw());

        Parent root = (Parent) loader.load();
        FXMLController controller = loader.getController();
        controller.setBackendMap(map);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.setTitle("Firmador");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        launch(args);

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
