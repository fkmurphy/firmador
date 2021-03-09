package org.openjfx.Main.models;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.openjfx.Main.file.FileRepository;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;

public class FilesToBeSigned {
    protected FileRepository file;
    protected CheckBox checked;
    protected Button signed;

    //
    public FilesToBeSigned(FileRepository file){
        this.file = file;
        this.checked = new CheckBox();
        this.signed = new Button();
        setStatus("");
        signed.setStyle(
                "-fx-background-color:none;"+
                        "-fx-border:none;"
        );
    }
    public FilesToBeSigned(FileRepository file, Boolean checked){
        this.file = file;
        this.checked = new CheckBox();
        this.checked.setSelected(checked);
        this.signed = new Button();
        setStatus("");
        signed.setStyle(
                "-fx-background-color:none;"+
                        "-fx-border:none;"
        );
    }

    public FileRepository getFile()
    {
        return this.file;
    }

    public String getFilePath(){
        return file.getPath();
    }

    public String getRepresentativePath() { return file.representativeName(); }

    public String getDescriptionFile() { return file.getDescription(); }

    public CheckBox getChecked(){
        return this.checked;
    }

    public void setChecked(boolean checked){
        this.checked.setSelected(checked);
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null || getClass() != obj.getClass())
            return false;
        return this.getRepresentativePath().compareTo(((FilesToBeSigned)obj).getRepresentativePath()) == 0;
    }

    public void setStatus(String status)
    {

        Text icon;
        switch (status) {
            case "signed":
                icon = FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.CHECK);
                icon.setFill(Color.GREEN);
                break;
            case "fail":
                icon = FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.CLOSE);
                icon.setFill(Color.RED);
                break;
            default:
                icon = FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.FILE_PDF_ALT);
                break;
        }
        //icon.setIconSize(15);
        this.signed.setGraphic(icon);
    }

    public void setSigned (Button btn)
    {
        this.signed = btn;
    }
    public Button getSigned ()
    {
        return this.signed;
    }

}
