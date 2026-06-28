package br.edu.ifba.controller.auth;

import br.edu.ifba.enums.TipoUsuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import br.edu.ifba.service.AuthService;
import br.edu.ifba.models.Usuario;
import br.edu.ifba.util.Sessao;
import br.edu.ifba.util.Tools;

import java.util.Objects;

import static br.edu.ifba.util.Tools.enviarAlerta;

public class Login {

    @FXML private AnchorPane rootPane;
    @FXML private ImageView estanteEsquerda;
    @FXML private ImageView estanteDireita;

    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;
    @FXML private Button entrarBtn;
    @FXML private Hyperlink cadastroLink;

    // ESSA FUNÇÃO É OBRIGATÓRIA PARA ELIMINAR A FRESTA DA TELA
    @FXML
    public void initialize() {
        if (rootPane != null && estanteEsquerda != null && estanteDireita != null) {
            // Força as imagens a escutarem e copiarem a altura da janela em tempo real
            estanteEsquerda.fitHeightProperty().bind(rootPane.heightProperty());
            estanteDireita.fitHeightProperty().bind(rootPane.heightProperty());
        }
    }

    @FXML
    public void login(ActionEvent event) {
        String email = campoEmail.getText();
        String senha = campoSenha.getText();

        System.out.println("Login acionado");
        System.out.println("Usuário: " + email);
        System.out.println("Senha: " + senha);

        Usuario userLogado = AuthService.login(email, senha);

        if (Objects.isNull(userLogado)) {
            enviarAlerta("Usuario não encontrado");
            return;
        }

        Sessao.setUsuarioLogado(userLogado);
        if (userLogado.getTipo().equals(TipoUsuario.ALUNO) || userLogado.getTipo().equals(TipoUsuario.PROFESSOR)) {
            Tools.navegarPara(event, "/views/usuarioViews/Catalogo.fxml");
            return;
        }

        Tools.navegarPara(event, "/views/bibliotecarioViews/dashboard.fxml");
    }

    @FXML
    public void fazerCadastro(ActionEvent event) {
        Tools.navegarPara(event, "/views/AuthViews/cadastro.fxml");
    }
}
