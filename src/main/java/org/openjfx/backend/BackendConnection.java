package org.openjfx.backend;

import javafx.util.converter.ByteStringConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;

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

            token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJCV0N6NDNLbnEwUmRDVmF2RkVGc1l1S01FRzZGd0Z6eEpQZUhSSUdtSS00In0.eyJleHAiOjE2MDE1MDIzNzEsImlhdCI6MTYwMTQ2NjM3MSwianRpIjoiM2ZhMGZiOWEtMzAyNC00MGNlLWIxZWMtY2Y0M2EwODMyZTFiIiwiaXNzIjoiaHR0cDovLzE5Mi4xNjguNDIuMjU6ODA4MC9hdXRoL3JlYWxtcy9JbnRlcm5vIiwiYXVkIjpbImZyb250ZW5kX3dvcmtmbG93IiwiYWNjb3VudCJdLCJzdWIiOiJiNzViOTE0My05NzBkLTQ0MTYtOGRhNy1hYWI5NDc2MmRhZjYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhZG1pbi1jbGkiLCJzZXNzaW9uX3N0YXRlIjoiZWQ4ZmFhMDYtMTAwYy00YmQ0LTllNjUtNjY5ZWRlNTNiMDQ1IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vMTkyLjE2OC40Mi4yNSIsImh0dHA6Ly8xMjcuMC4wLjEiLCIqIiwiaHR0cDovL2xvY2FsaG9zdDo0MjAwIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiZnJvbnRlbmRfd29ya2Zsb3ciOnsicm9sZXMiOlsiZnJvbnRlbmRfd29ya2Zsb3ciLCJtYW5hZ2VyX3NpdGUiLCJ1bWFfcHJvdGVjdGlvbiIsIm1hbmFnZXJfZG9jdW1lbnRhdGlvbiIsInNpZ25lciJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsInZpZXctYXBwbGljYXRpb25zIiwidmlldy1jb25zZW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJtYW5hZ2UtY29uc2VudCIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgQW5ndWxhclJvbGVzIGZyb250ZW5kX3JvbGVzIHBvc3RtYW5fcm9sZXMgcHJvZmlsZSBhZG1pbi1jbGkgZnJvbnRlbmRfd29ya2Zsb3ciLCJncm91cHNfbWVtYmVyIjpbXSwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiTsOpc3RvciBKdWxpw6FuIE1VUlBIWSIsInByZWZlcnJlZF91c2VybmFtZSI6IjIwMzc2NzI4MDMwIiwiZ2l2ZW5fbmFtZSI6Ik7DqXN0b3IgSnVsacOhbiIsImZhbWlseV9uYW1lIjoiTVVSUEhZIiwiZW1haWwiOiJqbXVycGh5QHRyaWJjdWVudGFzcmlvbmVncm8uZ292LmFyIn0.XFu2tQAS87VPlo2uenMjVY6Whf04LI-Ex4WYTvBb4c72DBIapny83DO1TQUGFDMcaINPQKBq_w1iNoj8H1RFJ4e3jhOOJQIKOta-Jjcm2jfJJ7qUBfhixzL77j1DK-9Q4X9XmarcOiBY4KaGQwOySBVtmtIGGOiulzYSWAZ6hJPMfhQpdRE7AKpNrsMkLBtTao_Ueg1zsGkX_qnSjt3yL0JbVAOAMnZW8INHzM6Er1Ka40ZzJLjrZtnd6FnPVd0CTn9eP56aQtaVArSRKbDJHKvJcTSdu3cPtPMVecBFnp0yfhDkPvxX41SkoYVg2pETBI5SIkAQuJIQlMF2jaEDcg";
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

    public boolean sendFile(String documentPath, int documentId)
    {
        //TODO if file exist
        boolean status = false;
        try {
            String boundary = Long.toHexString(System.currentTimeMillis());

            Map<Object, Object> data = new LinkedHashMap<>();
            data.put("document_id", documentId);
            data.put("file", Path.of(documentPath));
            data.put("ts", System.currentTimeMillis());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.url+"documents/upload"))
                    .POST(  ofMimeMultipartData(data, boundary))
                    .setHeader("Content-Type","multipart/form-data; boundary="+boundary)
                    .setHeader("Accept","application/json")
                    .setHeader("Authorization",this.token)
                    .build();

            HttpResponse<String> response = null;

            try {
                response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
                status = response.statusCode() == 200;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return status;


        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private HttpRequest.BodyPublisher ofMimeMultipartData(Map<Object, Object> data,
                                                         String boundary) throws IOException {
        List<byte[]> byteArrays = new ArrayList<>();

        byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=").getBytes(StandardCharsets.UTF_8);
        for (Map.Entry<Object, Object> entry : data.entrySet()) {

            byteArrays.add(separator);

            if (entry.getValue() instanceof Path) {
                var path = (Path) entry.getValue();
                String mimeType = Files.probeContentType(path);
                byteArrays.add(("\"" + entry.getKey() + "\"; filename=\"" + path.getFileName()
                        + "\"\r\nContent-Type: " + mimeType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                byteArrays.add(Files.readAllBytes(path));
                byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
            } else {
                byteArrays.add(("\"" + entry.getKey() + "\"\r\n\r\n" + entry.getValue() + "\r\n")
                        .getBytes(StandardCharsets.UTF_8));
            }
        }

        byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));

        return HttpRequest.BodyPublishers. ofByteArrays(byteArrays);
    }
}
