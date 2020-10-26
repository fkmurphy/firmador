package org.openjfx.Main.file;

import org.openjfx.backend.BackendConnection;
import org.openjfx.Main.file.helpers.PathHelper;
import org.openjfx.token.models.Token;


public class WorkflowFile implements FileRepository {
    int id, year, type, number, posX, posY;
    String description;

    public WorkflowFile(int id, int year, int type, int number, String description, int posX, int posY) {
        this.id = id;
        this.year = year;
        this.type = type;
        this.number = number;
        this.description = description;
        this.posX = posX;
        this.posY = posY;
    }

    public WorkflowFile(int id, int year, int type, int number) {
        this.id = id;
        this.year = year;
        this.type = type;
        this.number = number;
        this.description = "";
    }

    @Override
    public String getPath() {
        BackendConnection bk = BackendConnection.get();

        String dst = System.getProperty("java.io.tmpdir") + "/" +this.id+"_"+this.year+"_"+this.number+".pdf";
        bk.downloadFile("/documents/view/"+this.id, dst);

        return dst;
    }

    @Override
    public String representativeName() {
        return "N°: "+this.number + ", Tipo: " + this.type + " Año: "+ this.year;
    }

    @Override
    public String getDescription() { return this.description; }

    @Override
    public Boolean sign(Token token) {
        String srcPath = getPath();
        String dstFilename = PathHelper.generateDestionationPath(srcPath);
        if (dstFilename != null && dstFilename != ""){
            token.signWithPositionStamper(srcPath, dstFilename, posX, posY);
            BackendConnection.get().sendFile(dstFilename,this.id);
            return true;
        } else {
            return false;
        }
    }

}
