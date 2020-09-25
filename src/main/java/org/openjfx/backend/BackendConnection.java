package org.openjfx.backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.Duration;

public class BackendConnection {

    String token, url;
    HttpClient client;
    private static BackendConnection bkConnect;

    public static BackendConnection get(String parameters)
    {
        if (bkConnect == null) {
            bkConnect = new BackendConnection(parameters);
        }
        return bkConnect;
    }

    private BackendConnection (String parameters)
    {

            this.client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();

            token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJCV0N6NDNLbnEwUmRDVmF2RkVGc1l1S01FRzZGd0Z6eEpQZUhSSUdtSS00In0.eyJleHAiOjE2MDEwNzY1MTYsImlhdCI6MTYwMTA0MDUxNiwianRpIjoiMDFiYmZjZDUtNTNiNS00YWFhLTgzODktNDIyMTkzZjljNDBiIiwiaXNzIjoiaHR0cDovLzE5Mi4xNjguNDIuMjU6ODA4MC9hdXRoL3JlYWxtcy9JbnRlcm5vIiwiYXVkIjpbImZyb250ZW5kX3dvcmtmbG93IiwiYWNjb3VudCJdLCJzdWIiOiJiNzViOTE0My05NzBkLTQ0MTYtOGRhNy1hYWI5NDc2MmRhZjYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhZG1pbi1jbGkiLCJzZXNzaW9uX3N0YXRlIjoiOTY0ZTFkMzEtYWEwYi00Y2MxLWExNWQtMTBjOTAwNzY2NmRlIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vMTkyLjE2OC40Mi4yNSIsImh0dHA6Ly8xMjcuMC4wLjEiLCIqIiwiaHR0cDovL2xvY2FsaG9zdDo0MjAwIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiZnJvbnRlbmRfd29ya2Zsb3ciOnsicm9sZXMiOlsiZnJvbnRlbmRfd29ya2Zsb3ciLCJtYW5hZ2VyX3NpdGUiLCJ1bWFfcHJvdGVjdGlvbiIsIm1hbmFnZXJfZG9jdW1lbnRhdGlvbiIsInNpZ25lciJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsInZpZXctYXBwbGljYXRpb25zIiwidmlldy1jb25zZW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJtYW5hZ2UtY29uc2VudCIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgQW5ndWxhclJvbGVzIGZyb250ZW5kX3JvbGVzIHBvc3RtYW5fcm9sZXMgcHJvZmlsZSBhZG1pbi1jbGkgZnJvbnRlbmRfd29ya2Zsb3ciLCJncm91cHNfbWVtYmVyIjpbXSwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiTsOpc3RvciBKdWxpw6FuIE1VUlBIWSIsInByZWZlcnJlZF91c2VybmFtZSI6IjIwMzc2NzI4MDMwIiwiZ2l2ZW5fbmFtZSI6Ik7DqXN0b3IgSnVsacOhbiIsImZhbWlseV9uYW1lIjoiTVVSUEhZIiwiZW1haWwiOiJqbXVycGh5QHRyaWJjdWVudGFzcmlvbmVncm8uZ292LmFyIn0.ZgTxb2UImWGkq3r_-dBfl-l2sTq-j9Aztqqj3lYdcsQ6dCsKakJqlW6u4h7Xl2L46sNoHL3TMqqz-PGPxIsOzTtAUTf9v8th66AX2fiZfkio0jPdfe0iLbzLQhCXARIPkS6DYIMX5Ymz18BezYJXtxopFA6VtUv4YgASqHh5BVNipwhI4CQxV5mUQh9yJIMnEBnC7WPxQYhQVpjWVbtZdIDgbmPKHu9m3cs2_yOVF_N7ZYbXIetcwIoBly5WXDDXh7VttFnXP07BsV3T-oj2R7C0eYsogwG1F7vyFOGNyI8uXhnIKGAREooCy0rAk8hvErTRo1WbEA_4_rTR9psF3w";
            url = "http://192.168.42.25:8000/api/";
    }

    /**
     * documents
     * @param documents
     * @return HttpResponse documents
     */
    public HttpResponse<String> getRequest(String documents)
    {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(this.url+documents))
                .setHeader("Content-Type","application/json")
                .setHeader("Authorization",this.token)
                .build();
        HttpResponse<String> response = null;

        try {
            response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response;
    }

    public void downloadFile(String document, String dst)
    {
        try {
            URLConnection urlc = new URL(this.url+document).openConnection();
            urlc.setRequestProperty("Authorization",this.token);

            ReadableByteChannel rbc = Channels.newChannel(urlc.getInputStream());
            FileOutputStream fos = new FileOutputStream(dst);
            fos.getChannel().transferFrom(rbc,0, Long.MAX_VALUE);
        } catch (MalformedURLException e) {
            // TODO: 25/9/20   mensajes
            e.printStackTrace();
        } catch (IOException e) {
            // TODO: mensajes
            e.printStackTrace();
        }

    }
}
