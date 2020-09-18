package org.openjfx.models;

import javafx.scene.control.CheckBox;
import org.openjfx.file.FileRepository;

public class FilesToBeSigned {
    protected FileRepository file;
    protected CheckBox checked;
    public FilesToBeSigned(FileRepository file){
        this.file = file;
        this.checked = new CheckBox();
    }
    public FilesToBeSigned(FileRepository file, Boolean checked){
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

    @Override
    public boolean equals(Object obj){
        if(obj == null || getClass() != obj.getClass())
            return false;
        return this.getFilePath().compareTo(((FilesToBeSigned)obj).getFilePath()) == 0;

    }
}
