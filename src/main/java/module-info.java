module org.openjfx {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;
    requires org.kordamp.iconli.core;
    requires itextpdf;
    requires java.net.http;
    requires org.json;
    requires jcifs;
    requires pdfbox;
    requires pdf2dom;

    opens org.openjfx.models to javafx.base;
    opens org.openjfx;
}