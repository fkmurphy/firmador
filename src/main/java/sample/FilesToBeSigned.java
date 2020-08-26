package sample;

import file.PDF;
import javafx.scene.control.CheckBox;

public class FilesToBeSigned {
    protected PDF file;
    protected CheckBox checked;
    public FilesToBeSigned(PDF file){
        this.file = file;
        this.checked = new CheckBox();
    }
    public FilesToBeSigned(PDF file, Boolean checked){
        this.file = file;
        this.checked = new CheckBox();
        this.checked.setSelected(checked);
    }

    public String getFilePath(){
        return file.getPath();
    }
    public CheckBox getChecked(){
        return this.checked;
    }
    public void setChecked(CheckBox checked){
        this.checked = checked;
    }
}
