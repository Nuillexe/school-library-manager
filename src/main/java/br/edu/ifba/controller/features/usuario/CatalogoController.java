package br.edu.ifba.controller.features.usuario;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import br.edu.ifba.models.Titulo;
import br.edu.ifba.models.Usuario;
import br.edu.ifba.service.UsuarioService;
import br.edu.ifba.util.Sessao;
import br.edu.ifba.util.NavigationManager;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class CatalogoController implements Initializable {

    @FXML private ComboBox<String> comboCategorias;
    @FXML private VBox listaLivrosContainer;

    private UsuarioService usuarioService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Usuario logado = Sessao.getUsuarioLogado();
        this.usuarioService = new UsuarioService(logado);

        configurarCategorias();

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
        int disponiveis = t.getQuantidadeDeExemplaresDisponiveis();

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


        card.setOnMouseClicked(e -> abrirDetalhe(t));

        return card;
    }

    /*private void abrirDetalhe(Titulo t) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/usuario/DetalheLivro.fxml"));
            Parent root = loader.load();

            DetalheLivroController controller = loader.getController();

            controller.carregarLivro(t);

            Stage stage = (Stage) listaLivrosContainer.getScene().getWindow();
            Tools.trocarCenaPreservandoJanela(stage, root);

        } catch (IOException e) {
            System.err.println("Erro ao abrir detalhes do livro: " + t.getNome());
            e.printStackTrace();
        } catch (NullPointerException e){
            System.err.println("Tela n encontrada " + t.getNome());
            e.printStackTrace();
        }
    }*/

    private void abrirDetalhe(Titulo t){
        Sessao.setTituloSelecionado(t);
        NavigationManager.navegarPara(comboCategorias,"/views/usuario/DetalheLivro.fxml" );
    }

}