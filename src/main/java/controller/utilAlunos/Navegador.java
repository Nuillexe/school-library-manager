package com.biblioqueue.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Navegador {

    public static void ir(Stage stage, String fxmlPath) {
        try {
            boolean maximizado = stage.isMaximized();

            Parent root = FXMLLoader.load(Navegador.class.getResource(fxmlPath));

            stage.setMaximized(false);
            stage.setScene(new Scene(root));

            if (maximizado) {
                stage.setMaximized(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}