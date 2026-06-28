package br.edu.ifba.controller.auth;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import br.edu.ifba.models.Usuario;
import br.edu.ifba.service.AuthService;
import br.edu.ifba.util.Sessao;
import br.edu.ifba.util.Tools;

public class Cadastro {

    @FXML private AnchorPane rootPane;
    @FXML private ImageView estanteEsquerda;
    @FXML private ImageView estanteDireita;

    @FXML private TextField campoNome;
    @FXML private TextField campoEmail;
    @FXML private TextField campoId;
    @FXML private PasswordField campoSenha;
    @FXML private PasswordField campoConfirmarSenha;
    @FXML private Button btnRegistrar;
    @FXML private Hyperlink linkFazerLogin;

    // Método executado automaticamente pelo JavaFX assim que o FXML termina de carregar
    @FXML
    public void initialize() {
        if (rootPane != null && estanteEsquerda != null && estanteDireita != null) {
            // Vincula dinamicamente a altura das estantes à altura total da janela
            estanteEsquerda.fitHeightProperty().bind(rootPane.heightProperty());
            estanteDireita.fitHeightProperty().bind(rootPane.heightProperty());
        }
    }

    @FXML
    public void cadastrar(ActionEvent event) {
        String nome = campoNome.getText();
        String email = campoEmail.getText();
        String id = campoId.getText();
        String senha = campoSenha.getText();
        String confirmaSenha = campoConfirmarSenha.getText();

        if (!senha.equals(confirmaSenha)) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Erro no Cadastro");
            alerta.setHeaderText("As senhas não coincidem!");
            alerta.setContentText("Por favor, verifique se digitou a mesma senha nos dois campos.");
            alerta.showAndWait();
            return;
        }

        System.out.println("Tentativa de Cadastro:");
        System.out.println("Nome: " + nome);
        System.out.println("E-mail: " + email);
        System.out.println("Matrícula: " + id);

        Usuario novoUsuario = AuthService.cadastro(nome, email, senha, id);
        if (novoUsuario != null) {
            Tools.enviarAlerta("Registro Realizado com sucesso");
            Sessao.setUsuarioLogado(novoUsuario);
            Tools.navegarPara(event, "/views/usuarioViews/Catalogo.fxml");
        } else {
            Tools.enviarAlerta("Registro Realizado não sucedido");
        }
    }

    @FXML
    public void irParaLogin(ActionEvent event) {
        Tools.navegarPara(event, "/views/AuthViews/login.fxml");
    }
}