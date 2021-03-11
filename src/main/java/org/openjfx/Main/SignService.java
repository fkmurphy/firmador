package org.openjfx.Main;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.openjfx.Main.file.exceptions.BadPasswordTokenException;
import org.openjfx.Main.models.FilesToBeSigned;
import org.openjfx.components.PopupComponent;
import org.openjfx.infrastructure.Log;
import org.openjfx.token.models.GemaltoToken;

import java.util.Iterator;

public class SignService extends Service<Boolean> {
    private GemaltoToken token;
    private ObservableList<FilesToBeSigned> listItems;
    private final static Log LOGGER = new Log();

    public SignService(ObservableList<FilesToBeSigned> listItems, GemaltoToken token) {
        this.listItems = listItems;
        this.token = token;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>(){
            @Override
            protected Boolean call() throws Exception {
                Iterator<FilesToBeSigned> listFilesSrc = listItems.iterator();
                FilesToBeSigned fileSrc;

                while(listFilesSrc.hasNext()) {
                    fileSrc = listFilesSrc.next();
                    if (fileSrc.getChecked().isSelected()) {
                        try {
                            if  (!fileSrc.getFile().sign(token)) {
                                fileSrc.setStatus("fail");
                            } else {
                                fileSrc.setStatus("signed");
                            }
                        } catch (Exception e) {
                            fileSrc.setStatus("fail");
                        }
                    }
                }
                return true;
            }

        };
    }
}
