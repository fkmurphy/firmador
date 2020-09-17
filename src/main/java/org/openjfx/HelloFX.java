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
                .setHeader("Authorization","Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJCV0N6NDNLbnEwUmRDVmF2RkVGc1l1S01FRzZGd0Z6eEpQZUhSSUdtSS00In0.eyJleHAiOjE2MDAzODE3OTIsImlhdCI6MTYwMDM0NTc5MiwianRpIjoiMzdjMzg2MDgtMmQwZS00NzdmLThkM2ItNjAwMDJjYzE5MGQ1IiwiaXNzIjoiaHR0cDovLzE5Mi4xNjguNDIuMjU6ODA4MC9hdXRoL3JlYWxtcy9JbnRlcm5vIiwiYXVkIjpbImZyb250ZW5kX3dvcmtmbG93IiwiYWNjb3VudCJdLCJzdWIiOiJiNzViOTE0My05NzBkLTQ0MTYtOGRhNy1hYWI5NDc2MmRhZjYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhZG1pbi1jbGkiLCJzZXNzaW9uX3N0YXRlIjoiZDQyMTdhMDEtMmMxMS00Y2M4LWIxNDEtZTA0YzY5MDhlZTA0IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vMTkyLjE2OC40Mi4yNSIsImh0dHA6Ly8xMjcuMC4wLjEiLCIqIiwiaHR0cDovL2xvY2FsaG9zdDo0MjAwIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiZnJvbnRlbmRfd29ya2Zsb3ciOnsicm9sZXMiOlsiZnJvbnRlbmRfd29ya2Zsb3ciLCJtYW5hZ2VyX3NpdGUiLCJ1bWFfcHJvdGVjdGlvbiIsIm1hbmFnZXJfZG9jdW1lbnRhdGlvbiIsInNpZ25lciJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsInZpZXctYXBwbGljYXRpb25zIiwidmlldy1jb25zZW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJtYW5hZ2UtY29uc2VudCIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgQW5ndWxhclJvbGVzIGZyb250ZW5kX3JvbGVzIHBvc3RtYW5fcm9sZXMgcHJvZmlsZSBhZG1pbi1jbGkgZnJvbnRlbmRfd29ya2Zsb3ciLCJncm91cHNfbWVtYmVyIjpbXSwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiTsOpc3RvciBKdWxpw6FuIE1VUlBIWSIsInByZWZlcnJlZF91c2VybmFtZSI6IjIwMzc2NzI4MDMwIiwiZ2l2ZW5fbmFtZSI6Ik7DqXN0b3IgSnVsacOhbiIsImZhbWlseV9uYW1lIjoiTVVSUEhZIiwiZW1haWwiOiJqbXVycGh5QHRyaWJjdWVudGFzcmlvbmVncm8uZ292LmFyIn0.NDWjesFbIM1Pq0VTxYkMrL0KtvwZZSjN0mq47s6RT7hIkMig8YqtFa-PRN_v0Wefr6a2l_c6ZjMMME-2obNUDCXDRdKm3vYZOMCkh5kSIEg-IaIl9j_M21l4J4Ozr86P_N4vczf0dnJvReZkii2375h3519MxrGJ4gbP6gorTwNfhKBw9QrjEaO8VzO0gEQ0nUy3BtECjn4ZXXEGhvhT9ulJXvncHp6ly4oeuzonnQNei9Eo7INnhtiyT-h9ArU0U9Ouz41dg48dHzajDVzGNgeIvI6bArPfBtk8WKkhUPqk43NPLApGI8-bxa0gwpdnOExV-djy234Hw2hURj-anw")
                .build();
        HttpResponse<String> response = lala.send(request,HttpResponse.BodyHandlers.ofString());
        HttpHeaders headers = response.headers();
        headers.map().forEach((k,v) -> System.out.println(k+":"+v));
        JSONArray array = new JSONArray(response.body());

        System.out.println(array.toString());
        String path;
        byte[] buffer = new byte[1024];
        for (int i =0;i<array.length();i++){
            path = ((JSONObject)array.get(i)).get("path_original").toString();
            SMBAuth auth = new SMBAuth();
            jcifs.Config.setProperty("jcifs.netbios.wins","10.114.75.1");
            NtlmPasswordAuthentication la = new NtlmPasswordAuthentication("example","example","example");
            SmbFile in = new SmbFile("smb://10.114.x.x/x/PDF/231/2020/25.pdf", la);
            /*OutputStream pre = new SmbFileOutputStream(in);
            pre.write("adqweqweqweqwe".getBytes());
            System.out.println(in.canRead());
            if (in.exists()){
                System.out.println("existe");

                System.out.println(in.getCanonicalPath()+in.getDate());
            } else {
                System.out.println("no existe");
            }*/
            InputStream pre = new SmbFileInputStream(in);
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
