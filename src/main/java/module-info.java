module Firmador {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires jdk.crypto.cryptoki;
    requires itextpdf;

    opens sample;
    opens token;
}