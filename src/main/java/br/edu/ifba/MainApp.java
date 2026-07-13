package br.edu.ifba;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/auth_views/login.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Sistema de Biblioteca");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(false); // Truque: reseta para forçar o JavaFX a recalcular
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}