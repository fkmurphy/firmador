module org.openjfx {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires jdk.crypto.cryptoki;
    requires bcprov.jdk15on;
    requires itextpdf;
    requires java.net.http;
    requires org.json;
    requires jcifs;
    requires de.jensd.fx.glyphs.fontawesome;
    requires org.apache.pdfbox;


    opens org.openjfx.Main.models to javafx.base;
    opens org.openjfx.Main;
    opens org.openjfx;
}