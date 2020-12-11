package org.openjfx.components;

import javafx.animation.PauseTransition;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.w3c.dom.events.MouseEvent;

public class PopupComponent {
    protected Popup popup;
    protected Stage stage;

    public PopupComponent(String message, Window stage)
    {
        popup = new Popup();
        popup.setAutoHide(false);
        popup.setAutoFix(true);
        popup.setHideOnEscape(true);
        Label text = new Label(message);
        text.setMinWidth(50);
        text.setMaxWidth(400);
        text.setStyle("-fx-background-color:black;"
                + " -fx-text-fill: " + "white" + ";"
                + " -fx-font-size: " + "1em" + ";"
                + " -fx-padding: 10px;"
                + " -fx-background-radius: 6;");
        popup.getContent().add(text);
        popup.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                popup.setX(stage.getX() + stage.getWidth()/2 - popup.getWidth()/2);
                popup.setY(stage.getY() + stage.getHeight() / 4);
            }
        });
        //popup.setX(stage.getX() + stage.getWidth() / 2- popup.getWidth() / 2); //dialog.getWidth() = NaN
        //popup.setY(stage.getY());// - popup.getHeight() / 2); //dialog.getHeight() = NaN


    }

    public Popup showPopup() {

        PauseTransition delay = new PauseTransition(javafx.util.Duration.seconds(5));
        delay.setOnFinished(event -> popup.hide());
        delay.play();
        popup.setOpacity(0.5);
        return popup;
    }
}
