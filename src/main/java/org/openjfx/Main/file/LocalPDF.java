package org.openjfx.Main.file;

import org.openjfx.Main.file.exceptions.BadPasswordTokenException;
import org.openjfx.Main.file.helpers.PathHelper;
import org.openjfx.token.models.Token;

import java.security.GeneralSecurityException;

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
    public Boolean sign(Token token) throws BadPasswordTokenException {
        String dstFilename = PathHelper.generateDestionationPath(this.path);
        if (dstFilename != null && dstFilename != ""){
            try {
                token.sign(getPath(), dstFilename);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();

                return false;
            }
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
