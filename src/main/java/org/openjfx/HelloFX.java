package org.openjfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jcifs.smb.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;


public class HelloFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));
        HttpClient lala = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://192.168.42.25:8000/api/documents"))
                .setHeader("Content-Type","application/json")
                .setHeader("Authorization","Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJCV0N6NDNLbnEwUmRDVmF2RkVGc1l1S01FRzZGd0Z6eEpQZUhSSUdtSS00In0.eyJleHAiOjE2MDA0NzIxNTgsImlhdCI6MTYwMDQzNjE1OCwianRpIjoiZmU5YmEwNzQtNmJmZi00YTVjLTk5NjQtYTQwZmNhODU1YzNmIiwiaXNzIjoiaHR0cDovLzE5Mi4xNjguNDIuMjU6ODA4MC9hdXRoL3JlYWxtcy9JbnRlcm5vIiwiYXVkIjpbImZyb250ZW5kX3dvcmtmbG93IiwiYWNjb3VudCJdLCJzdWIiOiJiNzViOTE0My05NzBkLTQ0MTYtOGRhNy1hYWI5NDc2MmRhZjYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhZG1pbi1jbGkiLCJzZXNzaW9uX3N0YXRlIjoiYjRmYjIxNTAtYzQ4MS00YjY4LTg0NmQtY2MyYTE5ZmE1YTdkIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vMTkyLjE2OC40Mi4yNSIsImh0dHA6Ly8xMjcuMC4wLjEiLCIqIiwiaHR0cDovL2xvY2FsaG9zdDo0MjAwIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiZnJvbnRlbmRfd29ya2Zsb3ciOnsicm9sZXMiOlsiZnJvbnRlbmRfd29ya2Zsb3ciLCJtYW5hZ2VyX3NpdGUiLCJ1bWFfcHJvdGVjdGlvbiIsIm1hbmFnZXJfZG9jdW1lbnRhdGlvbiIsInNpZ25lciJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsInZpZXctYXBwbGljYXRpb25zIiwidmlldy1jb25zZW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJtYW5hZ2UtY29uc2VudCIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgQW5ndWxhclJvbGVzIGZyb250ZW5kX3JvbGVzIHBvc3RtYW5fcm9sZXMgcHJvZmlsZSBhZG1pbi1jbGkgZnJvbnRlbmRfd29ya2Zsb3ciLCJncm91cHNfbWVtYmVyIjpbXSwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiTsOpc3RvciBKdWxpw6FuIE1VUlBIWSIsInByZWZlcnJlZF91c2VybmFtZSI6IjIwMzc2NzI4MDMwIiwiZ2l2ZW5fbmFtZSI6Ik7DqXN0b3IgSnVsacOhbiIsImZhbWlseV9uYW1lIjoiTVVSUEhZIiwiZW1haWwiOiJqbXVycGh5QHRyaWJjdWVudGFzcmlvbmVncm8uZ292LmFyIn0.GFCgrt723bHcVYSJmAcBf1fSUiRXXtJ3MasYNjEmM6FOLhm0Vm4HjZwQT5VpDF6hFyqHnC_HMBPCYh1PFEinwKfoYIJOC8zyO9poX_NHKtoRZ79D46nRe7llZW81ol0CvOn50iaqWgoDeGqxw6ee6k7YZQT3W_nJMiROizyXrDyIyRybs_TlYqqHRlZ6lUXpQR0pPfnPqMa0d4Bf-klhfD4xRssqnmaUdbBMN367XIvO22CqfRofVTwvX1fyQ-qetks73ZlpLUaAk6CC3oNHtNBWqQJ_hJMLBjW2Rgky2s8--eze26C6dtjqNCtoHPINx_OptMlFJ0VIQ40OULFTww")
                .build();
        HttpResponse<String> response = lala.send(request,HttpResponse.BodyHandlers.ofString());
        HttpHeaders headers = response.headers();
        headers.map().forEach((k,v) -> System.out.println(k+":"+v));
        System.out.println(response.body());
        JSONArray array = new JSONArray(response.body());

        System.out.println(array.toString());
        String path;
        byte[] buffer = new byte[1024];
        for (int i =0;i<array.length();i++){
            path = ((JSONObject)array.get(i)).get("path_original").toString();

            /*OutputStream pre = new SmbFileOutputStream(in);
            pre.write("adqweqweqweqwe".getBytes());
            System.out.println(in.canRead());
            if (in.exists()){
                System.out.println("existe");

                System.out.println(in.getCanonicalPath()+in.getDate());
            } else {
                System.out.println("no existe");
            }*/
            //InputStream pre = new SmbFileInputStream(in);
            /*PDFDomTree parser = new PDFDomTree();
            PDDocument pdf = PDDocument.load(pre);
            System.out.println(parser.createDOM(pdf).toString());*/

        }

        System.out.println(response.body());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.setTitle("JavaFX and Gradle");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }

}
