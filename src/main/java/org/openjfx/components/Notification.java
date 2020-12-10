package org.openjfx.components;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Notification {

    static final double BORDER_RADIUS = 4;

    static void clipChildren (Region region, double arc) {
        final Rectangle outputClip = new Rectangle();
        outputClip.setArcWidth(arc);
        outputClip.setArcHeight(arc);
        region.setClip(outputClip);

        region.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            outputClip.setWidth(newValue.getWidth());
            outputClip.setHeight(newValue.getHeight());
        });
    }

    static Border createBorder () {
        return new Border(
                new BorderStroke (
                        Color.BLACK,
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(BORDER_RADIUS),
                        BorderStroke.THICK
                )
        );
    }

    static Shape createShape() {
        final Ellipse shape = new Ellipse(50,50);
        shape.setCenterX(80);
        shape.setCenterY(80);
        shape.setFill(Color.LIGHTCORAL);
        shape.setStroke(Color.LIGHTCORAL);
        return shape;
    }

    public static Region createClipped() {
        final Pane pane = new Pane(createShape());

        pane.setBorder(createBorder());
        pane.setPrefSize(100,100);

        clipChildren(pane, 3* BORDER_RADIUS);

        return pane;
    }
}
