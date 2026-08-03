package br.edu.ifba.util;

import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Node;

import java.io.IOException;

import javafx.fxml.FXMLLoader;

public class NavigationManager {

    public static void navegarPara(Event event, String caminhoFXML) {
        try {
            Parent root = FXMLLoader.load(NavigationManager.class.getResource(caminhoFXML));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            trocarCenaPreservandoJanela(stage, root);

        } catch (IOException e) {
            AlertManager.alertar("Erro ao carregar a tela: " + caminhoFXML);

            System.err.println("Erro ao carregar a tela: " + caminhoFXML);
            System.err.println("ERRO:" + e);
            e.printStackTrace();
        }
    }

    public static void navegarPara(Node node, String caminhoFXML) {
        try {
            Parent root = FXMLLoader.load(NavigationManager.class.getResource(caminhoFXML));

            Stage stage = (Stage) node.getScene().getWindow();

            trocarCenaPreservandoJanela(stage, root);

        } catch (IOException e) {
            AlertManager.showError("Erro ao carregar a tela: " + caminhoFXML);

            System.err.println("Erro ao carregar a tela: " + caminhoFXML);
            System.err.println("ERRO:" + e);
            e.printStackTrace();
        }
    }

    public static void trocarCenaPreservandoJanela(Stage stage, Parent root) {
        if (stage.getScene() == null) {
            stage.setScene(new Scene(root));
        } else {
            stage.getScene().setRoot(root);
        }

        stage.show();
    }

}
