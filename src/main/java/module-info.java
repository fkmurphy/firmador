module Firmador {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires jdk.crypto.cryptoki;
    requires itextpdf;

    opens init.controllers  to javafx.fxml;
    opens init.models to javafx.base;

    opens token.controllers to javafx.fxml;
    opens token.models to javafx.base;

    opens init;
    opens token;
}