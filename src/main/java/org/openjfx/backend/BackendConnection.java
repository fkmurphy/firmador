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

            token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJCV0N6NDNLbnEwUmRDVmF2RkVGc1l1S01FRzZGd0Z6eEpQZUhSSUdtSS00In0.eyJleHAiOjE2MDE1OTI0MjgsImlhdCI6MTYwMTU1NjQyOCwianRpIjoiZTBlM2UzNzUtNWIwNy00N2ZhLWJiYWQtMWEwZmQwZDQ5Yzg2IiwiaXNzIjoiaHR0cDovLzE5Mi4xNjguNDIuMjU6ODA4MC9hdXRoL3JlYWxtcy9JbnRlcm5vIiwic3ViIjoiYjVlMDhiYWYtMDgzYS00MzJiLThmMDUtYzQwZTE2N2ZlNjk5IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiZnJvbnRlbmRfd29ya2Zsb3ciLCJzZXNzaW9uX3N0YXRlIjoiNGI1NTM3MTMtNWJiOC00MTA3LTk1ZTItNzIzMmY1MjQ4M2ZlIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vMTkyLjE2OC40Mi4yNSIsImh0dHA6Ly8xMjcuMC4wLjEiLCJodHRwOi8vbG9jYWxob3N0OjQyMDAiXSwicmVzb3VyY2VfYWNjZXNzIjp7ImZyb250ZW5kX3dvcmtmbG93Ijp7InJvbGVzIjpbImZyb250ZW5kX3dvcmtmbG93IiwibWFuYWdlcl9zaXRlIiwidW1hX3Byb3RlY3Rpb24iLCJtYW5hZ2VyX2RvY3VtZW50YXRpb24iLCJzaWduZXIiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUgYWRtaW4tY2xpIGZyb250ZW5kX3dvcmtmbG93IiwiZ3JvdXBzX21lbWJlciI6WyIvUmlvX05lZ3JvIiwiL1JOX1RyaWJ1bmFsX0RlX0N1ZW50YXMiLCIvUk5URENfSW5mb3JtYXRpY2EiXSwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiSnVhbiBQYWJsbyBNYXJyb25pIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiMjMyODI1Njg1MTkiLCJnaXZlbl9uYW1lIjoiSnVhbiBQYWJsbyIsImZhbWlseV9uYW1lIjoiTWFycm9uaSIsImVtYWlsIjoiam1hcnJvbmlAZ21haWwuY29tIn0.XixGGxTlwpDrGfK5dsYDQWyOzMYPDucXXpbR5ZTWSP4-nfJKKydL4_GzgtDchHovhYLXqVE7DpAgvHJ0NbRO_x4sKmz7z00JHQnv3W88Zk4hhXxTmG6atshI9rX7S2Yo4VHrX3fF64roZA3L_pKtR0C-gxBl6QzkwOFDLNr_nbUAXod-86BbXsJQsJxkI7AYw_CfNIOjzdHZWl4Xjy1VuB5eprzJUE1RbxliBP3xyaKpFsYYa9TxREhvtBJP_UfrujWnIQg4hMA_bo_sF9Oztbmz4nQL4ie4eZSDnWBRfHZ7JTEB03TQq9NNdretyfWgkcKwrsMu5caIj0zRazNcVA";
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
