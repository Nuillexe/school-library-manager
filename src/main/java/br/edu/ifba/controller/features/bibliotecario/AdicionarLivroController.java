package br.edu.ifba.controller.features.bibliotecario;

import br.edu.ifba.models.Livro;
import br.edu.ifba.models.Usuario;
import br.edu.ifba.service.BibliotecarioService;
import br.edu.ifba.util.Sessao;
import br.edu.ifba.util.Tools;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class AdicionarLivroController implements Initializable {
    
    @FXML
    private BorderPane rootPane;

    @FXML
    private Label NomeUsuario;

    @FXML
    private TextField txtTitulo;

    @FXML
    private TextField txtIsbn;

    @FXML
    private TextField txtCategoria;

    @FXML
    private TextField txtAutor;

    @FXML
    private TextField txtDataDePublicacao;

    @FXML
    private TextField txtQuantidade;

    @FXML
    private TextArea txtDescricao;

    private BibliotecarioService bibliotecarioService;

    @FXML
    private void handleLogout(MouseEvent event) {
        try {
            Sessao.encerrarSessao();
            System.out.println("Logout realizado. Redirecionando para login...");
            
            Parent root = FXMLLoader.load(getClass().getResource("/views/auth_views/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Tools.trocarCenaPreservandoJanela(stage, root);
        } catch (IOException e) {
            System.err.println("Erro ao fazer logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSalvarLivro(ActionEvent event) {
        String titulo = txtTitulo.getText() != null ? txtTitulo.getText().trim() : "";
        String isbn = txtIsbn.getText() != null ? txtIsbn.getText().trim() : "";
        String categoria = txtCategoria != null && txtCategoria.getText() != null ? txtCategoria.getText().trim() : "";
        String autor = txtAutor.getText() != null ? txtAutor.getText().trim() : "";
        String quantidadeTexto = txtQuantidade != null && txtQuantidade.getText() != null ? txtQuantidade.getText().trim() : "";
        String descricao = txtDescricao != null && txtDescricao.getText() != null ? txtDescricao.getText().trim() : "";

        String textoData = (txtDataDePublicacao.getText() != null) ? txtDataDePublicacao.getText().trim() : "";
        LocalDate dataDePublicacao = null;

        if (!textoData.isEmpty()) {
            try {
                // 1. Define o formato esperado de digitação (Ex: 03/06/2026)
                java.time.format.DateTimeFormatter formatador = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

                // 2. Faz a conversão direta para LocalDate
                dataDePublicacao = LocalDate.parse(textoData, formatador);

                System.out.println("Data convertida com sucesso: " + dataDePublicacao);
            } catch (java.time.format.DateTimeParseException e) {
                System.err.println("Erro: A data digitada é inválida! Use o padrão dd/MM/yyyy");
                Tools.enviarAlerta("Erro: A data digitada é inválida! Use o padrão dd/MM/yyyy");
                return;
            }
        }

        if (titulo.isBlank() || isbn.isBlank() || categoria.isBlank() || autor.isBlank()
                || descricao.isBlank()) {
            Tools.enviarAlerta("Preencha todos os campos obrigatórios.");
            return;
        }

        final int quantidade;
        try {
            quantidade = Integer.parseInt(quantidadeTexto);
        } catch (NumberFormatException e) {
            Tools.enviarAlerta("Quantidade precisa ser um número válidos.");
            return;
        }

        if (quantidade <= 0) {
            Tools.enviarAlerta("A quantidade deve ser maior que zero.");
            return;
        }

        if (bibliotecarioService == null) {
            Tools.enviarAlerta("Serviço de bibliotecário indisponível.");
            return;
        }

        for (int i = 0; i < quantidade; i++) {
            Livro livro = new Livro(titulo, autor, isbn, categoria, descricao, dataDePublicacao);
            bibliotecarioService.adicionarLivro(livro);
            /*Tools.enviarAlerta(" Dados do livro:\n" +
                    "nome: "+livro.getNome()+"\n"+
                    "dataDePublicação: "+livro.getDataPublicacao()+"\n"+
                    "Descricao: "+livro.getDescricao()+"\n"+
                    "genero: "+livro.getGenero()+"\n"+
                    "Autor: "+livro.getAutor());*/
        }


        Tools.navegarPara(event, "/views/bibliotecario_views/inventario.fxml");
    }

    @FXML
    private void handleVoltar(MouseEvent event) {
        navegarPara("/views/bibliotecario_views/inventario.fxml", event);
    }

    @FXML
    private void dashboardController(MouseEvent event) {
        navegarPara("/views/bibliotecario_views/dashboard.fxml", event);
    }

    @FXML
    private void inventarioController(MouseEvent event) {
        navegarPara("/views/bibliotecario_views/inventario.fxml", event);
    }

    @FXML
    private void controleDeReservasController(MouseEvent event) {
        navegarPara("/views/bibliotecario_views/controleDeReservas.fxml", event);
    }

    @FXML
    private void controleDeEmprestimosController(MouseEvent event) {
        navegarPara("/views/bibliotecario_views/controleDeEmprestimos.fxml", event);
    }

    private void navegarPara(String fxmlPath, MouseEvent event) {
        try {
            System.out.println("Navegando para: " + fxmlPath);
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Tools.trocarCenaPreservandoJanela(stage, root);
        } catch (IOException e) {
            System.err.println("Erro ao navegar para " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Usuario logado = Sessao.getUsuarioLogado();
        if (logado != null) {
            NomeUsuario.setText(logado.getNome());
            bibliotecarioService = new BibliotecarioService(logado);
        }

        System.out.println("Adicionar Livro inicializado");
    }
}
