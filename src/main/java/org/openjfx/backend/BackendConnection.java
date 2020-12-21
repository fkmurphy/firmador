package org.openjfx.backend;

import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.converter.ByteStringConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.net.http.*;
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

    public static BackendConnection get()
    {
        return bkConnect;
    }
    public static BackendConnection get(Map<String,String> params)
    {
        if (bkConnect == null) {
            bkConnect = new BackendConnection(params);
        }
        return bkConnect;
    }

    private BackendConnection (Map<String,String> params)
    {
            this.client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();


            token = "Bearer " +  params.get("token");
            url = "http://" + params.get("api_url");
    }

    /**
     * documents
     * @param documents
     * @return HttpResponse documents
     */
    public HttpResponse<String> getRequest(String documents) throws HttpConnectTimeoutException, ConnectException, IOException, InterruptedException
    {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(this.url+documents))
                .setHeader("Content-Type","application/json")
                .setHeader("Authorization",this.token)
                .timeout(Duration.ofSeconds(5))
                .build();
        HttpResponse<String> response = null;

        try {
            response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (HttpConnectTimeoutException e) {
            //e.printStackTrace();
            throw new HttpConnectTimeoutException("timeout http connect");
        } catch (ConnectException e) {
            throw new ConnectException("check network");

        } catch (IOException e) {
            throw new IOException("-");
        } catch (InterruptedException e) {
            throw new InterruptedException(".");
        }
    }

    public void downloadFile(String document, String dst) throws MalformedURLException, IOException {
        URLConnection urlc = new URL(this.url+document).openConnection();
        urlc.setRequestProperty("Authorization",this.token);

        ReadableByteChannel rbc = Channels.newChannel(urlc.getInputStream());
        FileOutputStream fos = new FileOutputStream(dst);
        fos.getChannel().transferFrom(rbc,0, Long.MAX_VALUE);
        fos.close();
        //} catch (MalformedURLException e) {
        //} catch (IOException e) {
        //}
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
                    .uri(URI.create(this.url+"/documents/upload"))
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
