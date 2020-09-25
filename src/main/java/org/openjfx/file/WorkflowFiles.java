package org.openjfx.file;

import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.codec.Base64;
import org.openjfx.backend.BackendConnection;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.file.Files;


public class WorkflowFiles implements FileRepository {
    int id, year, type, number;

    public WorkflowFiles (int id, int year, int type, int number) {
        this.id = id;
        this.year = year;
        this.type = type;
        this.number = number;
    }

    @Override
    public String getPath() {
        BackendConnection bk = BackendConnection.get("");
        System.out.println("qweqwew"+this.id);
        FileOutputStream lala = null;
            //lala = new FileOutputStream("/tmp/"+this.id+"_"+this.year+"_"+this.number+".pdf");
            bk.downloadFile("documents/view/"+this.id, "/tmp/"+this.id+"_"+this.year+"_"+this.number+".pdf");
            //String body = bk.getRequest("documents/view/"+this.id).body();
            //lala.write(body.getBytes());

            //lala.flush();
            //lala.close();

        return "";
    }

    @Override
    public String representativeName() {
        return null;
    }
    //year
    //type
    //number
    //id
}
