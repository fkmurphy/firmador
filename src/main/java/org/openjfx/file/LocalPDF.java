package org.openjfx.file;

import jcifs.smb.SmbFileInputStream;
import org.openjfx.file.helpers.PathHelper;
import org.openjfx.token.models.Token;

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


    @Override
    public Boolean sign(Token token) {
        String dstFilename = PathHelper.generateDestionationPath(this.path);
        if (dstFilename != null && dstFilename != ""){
            token.sign(getPath(), dstFilename);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getDescription() {
        return "Archivo local";
    }

}
