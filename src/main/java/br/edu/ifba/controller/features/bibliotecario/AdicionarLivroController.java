package br.edu.ifba.controller.features.bibliotecario;

import br.edu.ifba.models.Livro;
import br.edu.ifba.models.Usuario;
import br.edu.ifba.service.BibliotecarioService;
import br.edu.ifba.util.AlertManager;
import br.edu.ifba.util.Sessao;
import br.edu.ifba.util.NavigationManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import java.time.LocalDate;
import java.net.URL;
import java.util.ResourceBundle;


public class AdicionarLivroController implements Initializable {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Usuario logado = Sessao.getUsuarioLogado();
        if (logado != null) {
            bibliotecarioService = new BibliotecarioService(logado);
        }

        System.out.println("Adicionar Livro inicializado");
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
                AlertManager.alertar("Erro: A data digitada é inválida! Use o padrão dd/MM/yyyy");
                return;
            }
        }

        if (titulo.isBlank() || isbn.isBlank() || categoria.isBlank() || autor.isBlank()
                || descricao.isBlank()) {
            AlertManager.alertar("Preencha todos os campos obrigatórios.");
            return;
        }

        final int quantidade;
        try {
            quantidade = Integer.parseInt(quantidadeTexto);
        } catch (NumberFormatException e) {
            AlertManager.alertar("Quantidade precisa ser um número válidos.");
            return;
        }

        if (quantidade <= 0) {
            AlertManager.alertar("A quantidade deve ser maior que zero.");
            return;
        }

        if (bibliotecarioService == null) {
            AlertManager.alertar("Serviço de bibliotecário indisponível.");
            return;
        }

        for (int i = 0; i < quantidade; i++) {
            Livro livro = new Livro(titulo, autor, isbn, categoria, descricao, dataDePublicacao);
            bibliotecarioService.adicionarLivro(livro);
            AlertManager.alertar(" Dados do livro recém criado:\n" +
                    "nome: "+livro.getNome()+"\n"+
                    "dataDePublicação: "+livro.getDataPublicacao()+"\n"+
                    "Descricao: "+livro.getDescricao()+"\n"+
                    "genero: "+livro.getGenero()+"\n"+
                    "Autor: "+livro.getAutor());
        }


        NavigationManager.navegarPara(event, "/views/bibliotecario/inventario.fxml");
    }

    @FXML
    private void handleVoltar(MouseEvent event) {
        NavigationManager.navegarPara(event, "/views/bibliotecario/inventario.fxml");
    }
}
