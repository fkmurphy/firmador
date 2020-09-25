package org.openjfx.file;

import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.codec.Base64;
import org.openjfx.backend.BackendConnection;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.file.Files;


public class WorkflowFile implements FileRepository {
    int id, year, type, number;

    public WorkflowFile(int id, int year, int type, int number) {
        this.id = id;
        this.year = year;
        this.type = type;
        this.number = number;
    }

    @Override
    public String getPath() {
        BackendConnection bk = BackendConnection.get("");

        String dst = System.getProperty("java.io.tmpdir") + "/" +this.id+"_"+this.year+"_"+this.number+".pdf";
        bk.downloadFile("documents/view/"+this.id, dst);

        return dst;
    }

    @Override
    public String representativeName() {
        return "un id: "+this.id;
    }
    //year
    //type
    //number
    //id
}
