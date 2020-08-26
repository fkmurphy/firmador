package sample;

import file.PDF;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private PasswordField password_token;
    @FXML
    private Button btn_select_file;
    @FXML
    private Button btn_firmar;
    @FXML
    private ObservableList<FilesToBeSigned> listitems = FXCollections.observableArrayList(
            new FilesToBeSigned(new PDF("qweqweqwe")),
            new FilesToBeSigned(new PDF("qweqweqweadasadassd"),true)
    );

    @FXML
    void firmarButton(ActionEvent event) {
        System.out.println(password_token.getText());
    }

    /**
     * Table and Columns
     */
    @FXML
    private TableView<FilesToBeSigned> table_files;
    @FXML
    private TableColumn<FilesToBeSigned, CheckBox> tb_check_file;
    @FXML
    private TableColumn<FilesToBeSigned, String> tb_path_file;

    @FXML
    void selectFile(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File fileSelected = fileChooser.showOpenDialog(new Stage());
        FilesToBeSigned newFile = new FilesToBeSigned(new PDF(fileSelected.getAbsolutePath()),true);
        table_files.getItems().add(newFile);
        //listitems.add(fileSelected.getAbsolutePath());
        //list_files.refresh();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //list_files.setItems(listitems);
        tb_check_file.setCellValueFactory(new PropertyValueFactory<FilesToBeSigned, CheckBox>("checked"));
        tb_path_file.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getFilePath()));
        table_files.setItems(listitems);

    }
}
