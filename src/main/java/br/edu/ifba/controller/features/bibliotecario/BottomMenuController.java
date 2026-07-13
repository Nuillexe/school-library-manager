package br.edu.ifba.controller.features.bibliotecario;

import br.edu.ifba.util.Sessao;
import br.edu.ifba.util.Tools;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;

public class BottomMenuController {

    @FXML
    private void irDashboard(MouseEvent event) {
        carregarTela(event, "/views/bibliotecario_views/dashboard.fxml");
    }

    @FXML
    private void irInventario(MouseEvent event) {
        carregarTela(event, "/views/bibliotecario_views/inventario.fxml");
    }

    @FXML
    private void irFilaReserva(MouseEvent event) {
        carregarTela(event, "/views/bibliotecario_views/controleDeReservas.fxml");
    }

    @FXML
    private void irDevolucoes(MouseEvent event) {
        carregarTela(event, "/views/bibliotecario_views/controleDeEmprestimos.fxml");
    }

    private void carregarTela(MouseEvent event, String caminhoFXML) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(caminhoFXML));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Tools.trocarCenaPreservandoJanela(stage, root);
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela " + caminhoFXML + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(MouseEvent event) {
        try {
            Sessao.encerrarSessao();
            System.out.println("Logout realizado via BottomMenu. Redirecionando para login...");
            
            Parent root = FXMLLoader.load(getClass().getResource("/views/auth_views/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Tools.trocarCenaPreservandoJanela(stage, root);
        } catch (IOException e) {
            System.err.println("Erro ao fazer logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
