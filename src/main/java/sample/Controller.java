package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.FileChooser;

public class Controller {
    @FXML
    private PasswordField password_token;

    @FXML
    private Button btn_firmar;

    @FXML
    void firmarButton(ActionEvent event) {
        System.out.println(password_token.getText());
    }


}
