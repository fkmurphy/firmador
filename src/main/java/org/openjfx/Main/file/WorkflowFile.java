package org.openjfx.Main.file;

import org.json.JSONObject;
import org.openjfx.Main.file.exceptions.BadPasswordTokenException;
import org.openjfx.backend.BackendConnection;
import org.openjfx.Main.file.helpers.PathHelper;
import org.openjfx.infrastructure.Log;
import org.openjfx.token.models.Token;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.security.GeneralSecurityException;


public class WorkflowFile implements FileRepository {
    int id, year, type, number, posX, posY;
    String description;
    String descriptionSignature;
    String imageSignature;
    private final static Log LOGGER = new Log();

    public WorkflowFile(int id, int year, int type, int number, String description, int posX, int posY) {
        this.id = id;
        this.year = year;
        this.type = type;
        this.number = number;
        this.description = description;
        this.posX = posX;
        this.posY = posY;
        this.descriptionSignature = null;
        this.imageSignature = null;
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

        try {
            String dst =  System.getProperty("java.io.tmpdir") + "/" +this.id+"_"+this.year+"_"+this.number+".pdf";
            bk.downloadFile("/documents/view/"+this.id, dst);
            return dst;
        } catch (IOException e) {
            LOGGER.warning("No se encontró el path del archivo. El documento se intentó descargar desde el backend."
                    + " Document " + this.id + " year " + this.year + " number " + this.number
                    + " :::response:" + e.getMessage()
            );
            //e.printStackTrace();
            return null;
        }

    }

    @Override
    public String representativeName() {
        return "N°: "+this.number + ", Tipo: " + this.type + " Año: "+ this.year;
    }

    @Override
    public String getDescription() { return this.description; }

    @Override
    public Boolean sign(Token token) throws BadPasswordTokenException {
        String srcPath = getPath();
        if (srcPath.length() <= 0) {
            return false;
        }

        String dstFilename = PathHelper.generateDestionationPath(srcPath);
        if (dstFilename != null && dstFilename != ""){
            try {
                setPosition(); // define position signer
                setSignatureInfo();
                token.signWithPositionStamper(srcPath, dstFilename, posX, posY, imageSignature,descriptionSignature);
            } catch (GeneralSecurityException e) {
                LOGGER.warning("ERROR, sign document: "
                        + this.id
                        + "/" + this.year
                        + "/" + this.number
                        + " :::exception_message:" + e.getMessage()
                );
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            BackendConnection.get().sendFile(dstFilename,this.id);
            return true;
        } else {
            LOGGER.warning("ERROR, destination file name is null. Document: "
                    + this.id
                    + "/" + this.year
                    + "/" + this.number
            );
            return false;
        }
    }

    private void setSignatureInfo() throws Exception {
        BackendConnection bk = BackendConnection.get();
        try {
            HttpResponse<String> response = bk.getRequest( "/people/signature-info");
            if (response.statusCode() != 200) {
                throw new Exception("Hubo un problema al obtener información para la estampa.");
            }
            JSONObject body = new JSONObject(response.body());
            imageSignature = (String) body.get("holographic_signature");
            descriptionSignature = (String) body.get("description_signature");
        } catch (IOException e) {
            e.printStackTrace();

            throw new Exception("Hubo un problema al obtener la posición de la estampa desde el servicio.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void setPosition() throws Exception {
        BackendConnection bk = BackendConnection.get();
        try {
            HttpResponse<String> response = bk.getRequest( "/signers/" + this.id + "/position");
            if (response.statusCode() != 200) {
                throw new Exception("Hubo un problema al obtener la posición de la estampa desde el servicio. La respuesta obtuvo un código diferente a 200.");
            }
            JSONObject body = new JSONObject(response.body());
            posX = (int) body.get("llx");
            posY = (int) body.get("lly");
        } catch (IOException e) {
            throw new Exception("Hubo un problema al obtener la posición de la estampa desde el servicio.");
        } catch (InterruptedException e) {

        }
    }

}
