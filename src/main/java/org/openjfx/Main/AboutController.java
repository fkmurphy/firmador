package org.openjfx.Main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutController implements Initializable {
    @FXML
    Label version;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        version.setText("Version: 1.0.5 - 06 Jul 2021");
    }

}
