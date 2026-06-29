package br.edu.ifba.controller.features.usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import br.edu.ifba.models.Titulo;
import br.edu.ifba.models.Usuario;
import br.edu.ifba.service.UsuarioService;
import br.edu.ifba.util.Sessao;
import br.edu.ifba.util.Tools;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class CatalogoController implements Initializable {

    @FXML private ComboBox<String> comboCategorias;
    @FXML private VBox listaLivrosContainer;
    @FXML private Label lblNomeUsuario;

    private UsuarioService usuarioService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Pega o usuário logado da Sessão e inicia o Service
        Usuario logado = Sessao.getUsuarioLogado();
        this.usuarioService = new UsuarioService(logado);

        // 2. Define o nome do usuário logado na interface
        if (logado != null && lblNomeUsuario != null) {
            lblNomeUsuario.setText(logado.getNome());
        }

        // 3. Configura o ComboBox
        configurarCategorias();

        // 4. Carrega os livros reais do sistema
        atualizarListaDeLivros(usuarioService.obterCatalogo());
    }

    private void configurarCategorias() {
        comboCategorias.getItems().clear();
        comboCategorias.getItems().add("Todas as categorias");

        // Pega as categorias direto dos títulos existentes para evitar erro de digitação
        List<String> generos = Arrays.stream(usuarioService.obterCatalogo())
                .map(Titulo::getGenero)
                .distinct()
                .toList();

        comboCategorias.getItems().addAll(generos);
        comboCategorias.getSelectionModel().selectFirst();
    }

    @FXML
    private void onCategoriaChanged() {
        String selecionada = comboCategorias.getValue();
        Titulo[] resultados;

        if (selecionada == null || selecionada.equals("Todas as categorias")) {
            resultados = usuarioService.obterCatalogo();
        } else {
            // Usa o método filtrarPorGenero do UsuarioService
            resultados = usuarioService.filtrarPorGenero(selecionada);
        }

        atualizarListaDeLivros(resultados);
    }

    /**
     * Limpa o container e renderiza os novos cards baseados no array de Titulos
     */
    private void atualizarListaDeLivros(Titulo[] titulos) {
        listaLivrosContainer.getChildren().clear();
        for (Titulo t : titulos) {
            VBox card = criarCardLivro(t);
            listaLivrosContainer.getChildren().add(card);
        }
    }

    private VBox criarCardLivro(Titulo t) {
        VBox card = new VBox(6);
        card.getStyleClass().add("book-card");

        Label titulo = new Label(t.getNome());
        titulo.getStyleClass().add("book-title");

        Label autor = new Label(t.getAutor());
        autor.getStyleClass().add("book-author");

        Label ano = new Label(String.valueOf(t.getDataPublicacao()));
        ano.getStyleClass().add("book-year");

        HBox tagRow = new HBox(8);
        tagRow.setPadding(new Insets(4, 0, 0, 0));

        Label tagCategoria = new Label(t.getGenero());
        tagCategoria.getStyleClass().add("tag-category");

        Label tagStatus;
        int disponiveis = t.getQuantidadeDisponivel();

        if (disponiveis > 0) {
            tagStatus = new Label(disponiveis + " disponível");
            tagStatus.getStyleClass().add("tag-disponivel");
        } else {
            tagStatus = new Label("Fila de espera");
            tagStatus.getStyleClass().add("tag-fila");
        }

        tagRow.getChildren().addAll(tagCategoria, tagStatus);
        card.getChildren().addAll(titulo, autor, ano, tagRow);

        // Adicione isso para o mouse virar a "mãozinha" ao passar por cima
        card.setStyle("-fx-cursor: hand;");

        // Ao clicar, você ainda usa a lógica do seu amigo para abrir detalhes
        card.setOnMouseClicked(e -> abrirDetalhe(t));

        return card;
    }

    private void abrirDetalhe(Titulo t) {
        try {
            // 1. Carrega o FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/usuarioViews/DetalheLivro.fxml"));
            Parent root = loader.load();

            // 2. Acessa o Controller da tela de detalhes
            DetalheLivroController controller = loader.getController();

            // 3. Passa o objeto Titulo para o controller (método que criamos anteriormente)
            controller.carregarLivro(t);

            // 4. Troca a cena
            Stage stage = (Stage) listaLivrosContainer.getScene().getWindow();
            Tools.trocarCenaPreservandoJanela(stage, root);

        } catch (IOException e) {
            System.err.println("Erro ao abrir detalhes do livro: " + t.getNome());
            e.printStackTrace();
        } catch (NullPointerException e){
            System.err.println("Tela n encontrada " + t.getNome());
            e.printStackTrace();
        }
    }

    @FXML private void onNavCatalogo() {
        System.out.println("Já está no Catálogo");
    }

    @FXML private void onNavEmprestimos() { navegarPara("/views/usuarioViews/Emprestimos.fxml"); }

    @FXML private void onNavReservas() { navegarPara("/views/usuarioViews/Reservas.fxml"); }

    // Método adicionado para realizar a ação de Logout e redirecionar para a autenticação
    @FXML
    private void onLogout() {
        try {
            Sessao.setUsuarioLogado(null);
            Parent root = FXMLLoader.load(getClass().getResource("/views/AuthViews/login.fxml"));
            Stage stage = (Stage) listaLivrosContainer.getScene().getWindow();
            Tools.trocarCenaPreservandoJanela(stage, root);
        } catch (IOException e) {
            System.err.println("Erro ao deslogar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navegarPara(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) listaLivrosContainer.getScene().getWindow();
            Tools.trocarCenaPreservandoJanela(stage, root);

        } catch (IOException e) {
            System.err.println("Erro ao navegar para: " + fxmlPath);
            e.printStackTrace();
        }
    }
}