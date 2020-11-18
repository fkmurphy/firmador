module org.openjfx {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;
    requires org.kordamp.ikonli.fontawesome5;
    requires jdk.crypto.cryptoki;
    requires bcprov.jdk15on;
    requires org.kordamp.iconli.core;
    requires itextpdf;
    requires java.net.http;
    requires org.json;
    requires jcifs;
    requires de.jensd.fx.glyphs.fontawesome;

    opens org.openjfx.Main.models to javafx.base;
    opens org.openjfx.Main;
    opens org.openjfx;
}