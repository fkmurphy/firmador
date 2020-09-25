package org.openjfx.file;

import jcifs.smb.SmbFileInputStream;

import java.io.InputStream;

public class LocalPDF implements FileRepository{
    protected String path;

    public LocalPDF(String path){
        this.path = path;
    }

    @Override
    public String getPath(){
        return this.path;
    }

    @Override
    public String representativeName() {
        return getPath();
    }

}
