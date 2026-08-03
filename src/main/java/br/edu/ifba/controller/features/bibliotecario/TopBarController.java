package br.edu.ifba.controller.features.bibliotecario;

import br.edu.ifba.util.Sessao;
import br.edu.ifba.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;

public class TopBarController {

    @FXML
    private ImageView logoutBtn;

    @FXML
    private Label NomeUsuario;

    @FXML
    void handleLogout(MouseEvent event) {
        try {
            // Encerrar a sessão
            Sessao.encerrarSessao();
            System.out.println("Sessão encerrada com sucesso");

            Parent root = FXMLLoader.load(getClass().getResource("/views/auth_views/login.fxml"));

            // Pega a janela (Stage) atual a partir do evento do clique
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            NavigationManager.trocarCenaPreservandoJanela(stage, root);

        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        NomeUsuario.setText(Sessao.getUsuarioLogado().getNome());
    }
}
