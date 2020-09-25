package org.openjfx.backend;

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
import java.nio.file.Files;
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

            token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJCV0N6NDNLbnEwUmRDVmF2RkVGc1l1S01FRzZGd0Z6eEpQZUhSSUdtSS00In0.eyJleHAiOjE2MDEwODMwMDIsImlhdCI6MTYwMTA0NzAwMiwianRpIjoiNDg5ZWMxYjQtNDUwYS00Mjc4LWI3NzUtMTk3NGRkMGY4ZmI4IiwiaXNzIjoiaHR0cDovLzE5Mi4xNjguNDIuMjU6ODA4MC9hdXRoL3JlYWxtcy9JbnRlcm5vIiwiYXVkIjpbImZyb250ZW5kX3dvcmtmbG93IiwiYWNjb3VudCJdLCJzdWIiOiJiNzViOTE0My05NzBkLTQ0MTYtOGRhNy1hYWI5NDc2MmRhZjYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhZG1pbi1jbGkiLCJzZXNzaW9uX3N0YXRlIjoiM2JmYjE5MTYtNDY0NS00OTdjLWJiYmMtYjY5NzZhYTlmNDM3IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vMTkyLjE2OC40Mi4yNSIsImh0dHA6Ly8xMjcuMC4wLjEiLCIqIiwiaHR0cDovL2xvY2FsaG9zdDo0MjAwIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiZnJvbnRlbmRfd29ya2Zsb3ciOnsicm9sZXMiOlsiZnJvbnRlbmRfd29ya2Zsb3ciLCJtYW5hZ2VyX3NpdGUiLCJ1bWFfcHJvdGVjdGlvbiIsIm1hbmFnZXJfZG9jdW1lbnRhdGlvbiIsInNpZ25lciJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsInZpZXctYXBwbGljYXRpb25zIiwidmlldy1jb25zZW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJtYW5hZ2UtY29uc2VudCIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgQW5ndWxhclJvbGVzIGZyb250ZW5kX3JvbGVzIHBvc3RtYW5fcm9sZXMgcHJvZmlsZSBhZG1pbi1jbGkgZnJvbnRlbmRfd29ya2Zsb3ciLCJncm91cHNfbWVtYmVyIjpbXSwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiTsOpc3RvciBKdWxpw6FuIE1VUlBIWSIsInByZWZlcnJlZF91c2VybmFtZSI6IjIwMzc2NzI4MDMwIiwiZ2l2ZW5fbmFtZSI6Ik7DqXN0b3IgSnVsacOhbiIsImZhbWlseV9uYW1lIjoiTVVSUEhZIiwiZW1haWwiOiJqbXVycGh5QHRyaWJjdWVudGFzcmlvbmVncm8uZ292LmFyIn0.KqdUTd_4l8EevMEbyJNfHp-7ZrXJ0A0Q3yZ2yUGPmt8QSNNkMM7f0Ldkazrt5exRN5FZumUgFs_9_3dQl--cFb2Nx9KYra1AhtcAS3HA_kXNmIB41c6hnnvrZaV1cz5aALZgPam2KZsEvlVVLoV7Zow3vKSPIcirhZvfXrYVN31M9ZXFM76hyiCEQFdRJNtIQiul5xdX8j8TDnmdWx4oCxA5FrcQw36gC2OJ0pmUwpiCK9p2gE6N9TInEot_azwoyTNo7IptW7axc745R2TarFzxP-SbqVRAaWYIIfvxVHcngh9PNj1a5RGDetIDBa0Md-67qy9kO5LD-RuNA3Yjlw";
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

    public boolean sendFile(String documentPath)
    {
        //TODO if file exist
        try {
            String boundary = Long.toHexString(System.currentTimeMillis());
            URLConnection connection = new URL(this.url+"documents/upload").openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type","multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("Authorization",this.token);
            System.out.println(documentPath+" ");
            File sourceFile = new File(documentPath);
            String CRLF = "\r\n";

            try(
                    OutputStream output = connection.getOutputStream();
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(output,"UTF-8"));
            ){
                // Send binary file.
                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"binaryFile\"; filename=\"" + sourceFile.getName() + "\"").append(CRLF);
                writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(sourceFile.getName())).append(CRLF);
                writer.append("Content-Transfer-Encoding: binary").append(CRLF);
                writer.append(CRLF).flush();
                Files.copy(sourceFile.toPath(), output);
                output.flush(); // Important before continuing with writer!
                writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

            }

            System.out.println(((HttpURLConnection) connection).getResponseMessage());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
