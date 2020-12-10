package org.openjfx.Main;
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
import javafx.animation.PauseTransition;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.HBox;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openjfx.backend.BackendConnection;
import org.openjfx.Main.file.LocalPDF;
import org.openjfx.Main.file.WorkflowFile;
import org.openjfx.Main.models.FilesToBeSigned;
import org.openjfx.components.Notification;
import org.openjfx.token.models.GemaltoToken;

import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {
    @FXML
    private PasswordField password_token;

    @FXML
    private MenuBar menuBar;

    /**
     * Buttons
     */
    @FXML
    private Button btn_select_file;
    //@FXML
    //private Button btn_firmar;


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
    private TableColumn<FilesToBeSigned, String> tb_description;
    @FXML
    private TableColumn<FilesToBeSigned,Button> tb_status_sign;

    Stage stage;

    GemaltoToken token = null;

    Map<String,String> mapArgument;

    @FXML
    private final ObservableList<FilesToBeSigned> listitems = FXCollections.observableArrayList(
            //        new FilesToBeSigned(new LocalPDF("qweqweqweadasadassd"),true)
    );
    private HostServices hostServices;

    public void setBackendMap(Map<String, String> map) {
        this.mapArgument = map;
        Thread backend = new Thread(this::processDocumentsBackend);
        backend.start();
    }

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    @FXML
    void firmarButton() {
        token = new GemaltoToken(password_token.getText());


        Iterator<FilesToBeSigned> listFilesSrc = listitems.iterator();
        FilesToBeSigned fileSrc;

        while(listFilesSrc.hasNext()){
            fileSrc = listFilesSrc.next();
            if (fileSrc.getChecked().isSelected()) {
                try {
                    if  (!fileSrc.getFile().sign(token)) {
                        fileSrc.setStatus("fail");
                    } else {
                        fileSrc.setStatus("signed");
                    }
                    fileSrc.setChecked(false);

                } catch (Exception e) {
                    fileSrc.setStatus("fail");
                }
            }
        }

        //String src = org.openjfx.HelloFX.class.getClassLoader().getResource("uno.pdf").getFile();
        //String dest = String.format("/home/jmurphy/hola2.pdf",1);
        //token.sign(src, String.format(dest, 1));
    }


    @FXML
    void selectFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Elegir un archivo");
        File fileSelected = fileChooser.showOpenDialog(new Stage());
        FilesToBeSigned newFile = new FilesToBeSigned(new LocalPDF(fileSelected.getAbsolutePath()),true);
        if(!listitems.contains(newFile)) {
            listitems.add(newFile);
        }
        /*
        Popup popup = new Popup();
        popup.setAutoHide(false);
        popup.setAutoFix(true);
        Label text = new Label("QWeqweqwe");
        text.setMinWidth(50);
        text.setPrefWidth(stage.getWidth());
        text.setMaxWidth(400);
        text.setStyle("-fx-background-color:black;"
                + " -fx-text-fill: " + "white" + ";"
                + " -fx-font-size: " + "1em" + ";"
                + " -fx-padding: 10px;"
                + " -fx-background-radius: 6;");
        popup.getContent().add(text);
        //popup.setX();
        //popup.setY();

        PauseTransition delay = new PauseTransition(javafx.util.Duration.seconds(5));
        delay.setOnFinished(event -> popup.hide());
        delay.play();
        popup.show(stage,
                stage.getX() + menuBar.getBoundsInLocal().getMinY(),
                stage.getY() + menuBar.getBoundsInLocal().getMaxY() + menuBar.getHeight()
        );*/
        //notificationPane.getChildren().add(Notification.createClipped());

        //listitems.add(fileSelected.getAbsolutePath());
        //list_files.refresh();

    }

    @FXML
    void btnTokenInfoAction() {
        try {
            Parent root  = FXMLLoader.load(getClass().getClassLoader().getResource("token_info.fxml"));
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
        tb_status_sign.setCellValueFactory(new PropertyValueFactory<FilesToBeSigned,Button>("signed"));
        table_files.setItems(listitems);
        tb_description.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDescriptionFile()));
        actionColumn();


        /*listitems.add(
                new FilesToBeSigned((FileRepository) new SambaConnection())
        );*/

    }

    private void processDocumentsBackend() {
        if (mapArgument == null || !mapArgument.containsKey("api_url"))
            return;

        BackendConnection bk;
        HttpResponse<String> response;
        try {
            bk =  BackendConnection.get(mapArgument);
            response = bk.getRequest("/documents/pending/");
            // TODO: 5/10/20 throwable
            if (response == null || response.statusCode() != 200)
                return;

            JSONArray array = new JSONArray(response.body());
            int id,type,number, year, posX, posY;
            String description;
            JSONObject json;
            WorkflowFile ll;
            for (int i =0;i<array.length();i++){
                json = (JSONObject)array.get(i);
                id =  Integer.parseInt(json.get("id").toString());
                year = Integer.parseInt(json.get("year").toString());
                type = Integer.parseInt(json.get("type").toString());
                number = Integer.parseInt(json.get("number").toString());
                description = json.get("theme").toString();
                posX = Integer.parseInt(json.get("posX").toString());
                posY = Integer.parseInt(json.get("posY").toString());
                ll = new WorkflowFile(id, year, type, number, description, posX, posY);
                listitems.add( new FilesToBeSigned(ll));
            }
        } catch (ConnectException e) {

            Platform.runLater(()->{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Error al conectarse.");
                alert.setContentText("verifique que tiene acceso a internet.");
                alert.showAndWait();

                /*
                Popup popup = new Popup();
                popup.setAutoHide(false);
                popup.setAutoFix(true);
                //popup.set
                popup.getContent().add(new Label("QWeqweqwe"));
                PauseTransition delay = new PauseTransition(javafx.util.Duration.seconds(3));
                delay.setOnFinished(event -> popup.hide());
                delay.play();
                popup.show(stage);*/
            });


        } catch (HttpConnectTimeoutException e) {
            Platform.runLater(()-> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Tiempo de espera agotado");
                alert.setContentText("Parece que ha tardado demasiado en adquirir los documentos.");
                alert.showAndWait();

            });
        } catch (IOException e) {
            Platform.runLater(()-> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("¡Error!");
                alert.setContentText("Hubo un problema con la conexión. ERR: #35501");
                alert.showAndWait();

            });
        } catch (InterruptedException e) {
        }

    }

    private void actionColumn(){
        TableColumn actionCol = new TableColumn("Action");

        Callback<TableColumn<String, String>, TableCell<String, String>> cellFactory
                = new Callback<TableColumn<String, String>, TableCell<String, String>>() {
            @Override
            public TableCell call(final TableColumn<String, String> param) {
                final TableCell<String, String> cell = new TableCell<String, String>() {
                    Text plusIcon = FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.MINUS,"15");
                    Text eyeIcon = FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.EYE,"15");
                    //final FontIcon plusIcon = new FontIcon("fa-minus");
                    final Button btnDelete = new Button();
                    final Button btnShowFile = new Button();
                    HBox pane = new HBox(btnDelete,btnShowFile);
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            //plusIcon.setIconSize(15);
                            btnDelete.setGraphic(plusIcon);
                            btnDelete.setStyle(
                                    "-fx-background-color:none;"+
                                            "-fx-border:none"
                            );

                            btnDelete.setOnMouseEntered(e->{
                                //plusIcon.setIconColor(Color.web("#ff5900",1.0));
                            });
                            btnDelete.setOnMouseExited(e -> {
                                //plusIcon.setIconColor(Color.web("#000",1.0));
                            });
                            btnDelete.setOnAction(event -> {
                                //Person person = getTableView().getItems().get(getIndex());
                                listitems.remove(getTableView().getItems().get(getIndex())) ;
                            });


                            btnShowFile.setGraphic(eyeIcon);
                            btnShowFile.setStyle(
                                    "-fx-background-color:none;"+
                                            "-fx-border:none"
                            );
                            btnShowFile.setOnMouseEntered(e->{
                                //plusIcon.setIconColor(Color.web("#ff5900",1.0));
                            });
                            btnShowFile.setOnMouseExited(e -> {
                                //plusIcon.setIconColor(Color.web("#000",1.0));
                            });
                            btnShowFile.setOnAction(event -> {

                                hostServices.showDocument(
                                        listitems.get(getIndex()).getFilePath()
                                );
                            });


                            setGraphic(pane);
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

    public void setGetHostController(HostServices hostServices) {
        this.hostServices = hostServices;
    }
}
