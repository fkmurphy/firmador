package init.models;

import shared.file.LocalPDF;
import javafx.scene.control.CheckBox;

public class FilesToBeSigned {
    protected LocalPDF file;
    protected CheckBox checked;
    public FilesToBeSigned(LocalPDF file){
        this.file = file;
        this.checked = new CheckBox();
    }
    public FilesToBeSigned(LocalPDF file, Boolean checked){
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
