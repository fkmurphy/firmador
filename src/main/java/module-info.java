module org.openjfx {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires jdk.crypto.cryptoki;
    //requires bcprov.jdk15on;
    //requires itextpdf;
    requires java.net.http;
    requires jcifs;
    requires de.jensd.fx.glyphs.fontawesome;
    requires org.update4j;
    requires java.logging;
    requires org.json;
    requires sign;
    requires kernel;


    opens org.openjfx.Main.models to javafx.base;
    opens org.openjfx.Main;
    opens org.openjfx;
}