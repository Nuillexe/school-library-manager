package br.edu.ifba.util;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Node;

import java.awt.event.MouseEvent;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;

public class Tools {

    public static  void enviarAlerta(String alerta){
        Alert aviso= new Alert(Alert.AlertType.INFORMATION);
        aviso.setTitle("Aviso");
        aviso.setHeaderText(null);
        aviso.setContentText(alerta);
        aviso.showAndWait();
    }


    public static void navegarPara(Event event, String caminhoFXML) {
        try {
            // Carrega o novo arquivo
            Parent root = FXMLLoader.load(Tools.class.getResource(caminhoFXML));

            // Pega o Stage atual
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            trocarCenaPreservandoJanela(stage, root);

        } catch (IOException e) {
            Tools.enviarAlerta("Erro ao carregar a tela: " + caminhoFXML);

            System.err.println("Erro ao carregar a tela: " + caminhoFXML);
            System.err.println("ERRO:" + e);
            e.printStackTrace();
        }
    }

    /**
     * Navega para uma nova tela a partir de um Node, preservando a maximização da janela.
     * Use este método em controllers que fazem navegação manualmente (sem ActionEvent).
     */


    public static void trocarCenaPreservandoJanela(Stage stage, Parent root) {
        boolean estaMaximizada = stage.isMaximized();
        double largura = stage.getWidth();
        double altura = stage.getHeight();

        stage.setScene(new Scene(root, largura, altura));

        if (estaMaximizada) {
            Platform.runLater(() -> stage.setMaximized(true));
        } else {
            stage.setWidth(largura);
            stage.setHeight(altura);
        }

        stage.show();
    }


}
