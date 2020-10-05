package org.openjfx;
/*
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class FXMLController implements Initializable {

    @FXML
    private Label label;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        label.setText("Hello, JavaFX " + javafxVersion + "\nRunning on Java " + javaVersion + ".");
    }
}
*/

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kordamp.ikonli.javafx.FontIcon;
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
import org.openjfx.backend.BackendConnection;
import org.openjfx.file.FileRepository;
import org.openjfx.file.LocalPDF;
import org.openjfx.file.WorkflowFile;
import org.openjfx.models.FilesToBeSigned;
import org.openjfx.token.models.GemaltoToken;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {
    @FXML
    private PasswordField password_token;
    @FXML
    private MenuItem mi_token;
    @FXML
    private Button btn_select_file;
    @FXML
    private Button btn_firmar;
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
    private TableColumn<FilesToBeSigned,Button> tb_button_info;
    @FXML
    private TableColumn<FilesToBeSigned, String> tb_description;

    GemaltoToken token = null;

    Map<String,String> mapArgument;

    @FXML
    private ObservableList<FilesToBeSigned> listitems = FXCollections.observableArrayList(

            //        new FilesToBeSigned(new LocalPDF("qweqweqweadasadassd"),true)
    );

    public void setBackendMap(Map<String, String> map) {
        this.mapArgument = map;
        processDocumentsBackend();
    }

    @FXML
    void firmarButton(ActionEvent event) {
        if(token == null)
            token = new GemaltoToken(password_token.getText());
        Iterator<FilesToBeSigned> listFilesSrc = listitems.iterator();
        FilesToBeSigned fileSrc;
        String dstStr, srcStr, extension;
        int indexDot;
        while(listFilesSrc.hasNext()){
            fileSrc = listFilesSrc.next();
            if(fileSrc.getChecked().isSelected())
                fileSrc.getFile().sign(token);
        }
//        String src = HelloFX.class.getClassLoader().getResource("uno.pdf").getFile();
        //String dest = String.format("/home/jmurphy/hola2.pdf",1);
        //token.sign(src, String.format(dest, 1));
    }


    @FXML
    void selectFile(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Elegir un archivo");
        File fileSelected = fileChooser.showOpenDialog(new Stage());
        FilesToBeSigned newFile = new FilesToBeSigned((FileRepository) new LocalPDF(fileSelected.getAbsolutePath()),true);
        if(!listitems.contains(newFile))
            listitems.add(newFile);
        //listitems.add(fileSelected.getAbsolutePath());
        //list_files.refresh();

    }

    @FXML
    void btnTokenInfoAction(ActionEvent event) {
        System.out.println("PPrueba boton");
        try {
            Parent root  = FXMLLoader.load(getClass().getClassLoader().getResource("token_info.fxml"));
            Stage stage = (Stage) btn_select_file.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //list_files.setItems(listitems);
        tb_check_file.setCellValueFactory(new PropertyValueFactory<FilesToBeSigned, CheckBox>("checked"));
        tb_path_file.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getRepresentativePath()));
        table_files.setItems(listitems);
        tb_description.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDescriptionFile()));
        actionColumn();


        /*listitems.add(
                new FilesToBeSigned((FileRepository) new SambaConnection())
        );*/

    }

    private void processDocumentsBackend() {
        if (!mapArgument.containsKey("api_url"))
            return;
        BackendConnection bk =  BackendConnection.get(mapArgument);
        HttpResponse<String> response = bk.getRequest("/documents/");

        // TODO: 5/10/20 throwable
        if (response.statusCode() != 200)
            return;

        JSONArray array = new JSONArray(response.body());

        int id,type,number, year;
        String description;
        byte[] buffer = new byte[1024];
        JSONObject json;
        WorkflowFile ll = null;
        for (int i =0;i<array.length();i++){
            json = (JSONObject)array.get(i);
            id =  Integer.parseInt(json.get("id").toString());
            year = Integer.parseInt(json.get("year").toString());
            type = Integer.parseInt(json.get("type").toString());
            number = Integer.parseInt(json.get("number").toString());
            description = json.get("theme").toString();
            ll = new WorkflowFile(id, year, type, number, description);
            listitems.add( new FilesToBeSigned((FileRepository) ll));
        }

    }

    private void actionButtonDelete(){

    }

    private void actionColumn(){
        TableColumn actionCol = new TableColumn("Action");

        Callback<TableColumn<String, String>, TableCell<String, String>> cellFactory
                = new Callback<TableColumn<String, String>, TableCell<String, String>>() {
            @Override
            public TableCell call(final TableColumn<String, String> param) {
                final TableCell<String, String> cell = new TableCell<String, String>() {
                    FontIcon plusIcon = new FontIcon("fa-minus");
                    Button btn = new Button();

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            plusIcon.setIconSize(15);
                            btn.setGraphic(plusIcon);
                            btn.setStyle(
                                    "-fx-background-color:none;"+
                                            "-fx-border:none"
                            );
                            btn.setOnMouseEntered(e->{
                                plusIcon.setIconColor(Color.web("#ff5900",1.0));
                            });
                            btn.setOnMouseExited(e -> {
                                plusIcon.setIconColor(Color.web("#000",1.0));
                            });
                            btn.setOnAction(event -> {
                                //Person person = getTableView().getItems().get(getIndex());
                                listitems.remove(getTableView().getItems().get(getIndex())) ;
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };

        //table.setItems(data);
        actionCol.setCellFactory(cellFactory);
        table_files.getColumns().addAll(actionCol);

    }


}
