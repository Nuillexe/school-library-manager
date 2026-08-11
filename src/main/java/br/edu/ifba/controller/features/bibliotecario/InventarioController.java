package br.edu.ifba.controller.features.bibliotecario;

import br.edu.ifba.models.Titulo;
import br.edu.ifba.service.BibliotecarioService;
import br.edu.ifba.util.Sessao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

import static br.edu.ifba.util.NavigationManager.navegarPara;

public class InventarioController implements Initializable {

    @FXML private FlowPane containerLivros;

    private BibliotecarioService service;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (Sessao.getUsuarioLogado() != null) {
            this.service = new BibliotecarioService(Sessao.getUsuarioLogado());
            renderizarInventario();
        }
    }

    private void renderizarInventario() {
        containerLivros.getChildren().clear(); // Limpa cards estáticos

        for (Titulo titulo : service.getB().getTitulos().listar()) {
            VBox card = criarCard(titulo);
            containerLivros.getChildren().add(card);
        }
    }

    private VBox criarCard(Titulo titulo) {
        VBox card = new VBox(15);
        card.setPrefWidth(500);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                "-fx-border-color: #E0E0E0; -fx-border-radius: 20; -fx-border-width: 1; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);");

        // Topo: Ícone e Título/Autor
        HBox header = new HBox(15);
        VBox iconBox = new VBox(new Label("📖"));
        iconBox.setAlignment(Pos.CENTER);
        iconBox.setMinSize(60, 60);
        iconBox.setStyle("-fx-background-color: #F3E5F5; -fx-background-radius: 15;");
        ((Label)iconBox.getChildren().get(0)).setStyle("-fx-font-size: 24px;");

        VBox infoBox = new VBox(2);
        Label lblNome = new Label(titulo.getNome());
        lblNome.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        Label lblAutor = new Label(titulo.getAutor());
        lblAutor.setStyle("-fx-text-fill: #888888;");
        infoBox.getChildren().addAll(lblNome, lblAutor);
        header.getChildren().addAll(iconBox, infoBox);

        // ID e Categoria (Tags)
        HBox tags = new HBox(10);
        Label tagCat = new Label(titulo.getGenero());
        tagCat.setStyle("-fx-background-color: #E9EEF7; -fx-text-fill: #1A3783; -fx-padding: 4 8; -fx-background-radius: 5; -fx-font-size: 12px; -fx-font-weight: bold;");
        tags.getChildren().addAll(tagCat);

        // Seção de Detalhes Técnicos (Ano e ISBN)
        VBox detalhesTecnicos = new VBox(6);
        detalhesTecnicos.setPadding(new Insets(12));
        detalhesTecnicos.setStyle("-fx-background-color: #F9F9F9; -fx-background-radius: 10;");

        // Linha do Ano de Lançamento
        HBox linhaAno = new HBox();
        Label lblTxtAno = new Label("Ano de lançamento:");
        lblTxtAno.setStyle("-fx-text-fill: #777777; -fx-font-size: 13px;");
        HBox spacerAno = new HBox();
        HBox.setHgrow(spacerAno, Priority.ALWAYS);
        Label lblAnoValor = new Label(String.valueOf(titulo.getDataPublicacao()));
        lblAnoValor.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333;");
        linhaAno.getChildren().addAll(lblTxtAno, spacerAno, lblAnoValor);

        // Linha do ISBN
        HBox linhaIsbn = new HBox();
        Label lblTxtIsbn = new Label("ISBN:");
        lblTxtIsbn.setStyle("-fx-text-fill: #777777; -fx-font-size: 13px;");
        HBox spacerIsbn = new HBox();
        HBox.setHgrow(spacerIsbn, Priority.ALWAYS);
        Label lblIsbnValor = new Label(titulo.getIsbn());
        lblIsbnValor.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333; -fx-font-family: 'Courier New';"); // Fonte courier para cara de código/serial
        linhaIsbn.getChildren().addAll(lblTxtIsbn, spacerIsbn, lblIsbnValor);

        detalhesTecnicos.getChildren().addAll(linhaAno, linhaIsbn);

        // Seção de Estoque / Quantidade
        VBox estoqueBox = new VBox(6);

        // Total no acervo
        HBox totalAcervo = new HBox();
        Label txtTotal = new Label("Total no acervo:");
        txtTotal.setStyle("-fx-text-fill: #555555;");
        HBox spacerTotal = new HBox();
        HBox.setHgrow(spacerTotal, Priority.ALWAYS);
        Label lblTotalQtd = new Label(titulo.getQuantidadeDeExemplares() + " unidades");
        lblTotalQtd.setStyle("-fx-text-fill: #555555;");
        totalAcervo.getChildren().addAll(txtTotal, spacerTotal, lblTotalQtd);

        // Disponíveis para empréstimo
        HBox disponiveisAcervo = new HBox();
        Label txtEstoque = new Label("Disponíveis agora:");
        HBox spacerDisp = new HBox();
        HBox.setHgrow(spacerDisp, Priority.ALWAYS);

        int disponiveis = titulo.getQuantidadeDisponivel();
        Label lblQtd = new Label(disponiveis + " exemplares");

        // Se houver exemplares disponíveis fica verde, se for 0 fica vermelho
        if (disponiveis > 0) {
            lblQtd.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
        } else {
            lblQtd.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
        }
        disponiveisAcervo.getChildren().addAll(txtEstoque, spacerDisp, lblQtd);

        estoqueBox.getChildren().addAll(totalAcervo, disponiveisAcervo);

        // Adiciona todos os elementos estruturados no card principal
        card.getChildren().addAll(header, tags, detalhesTecnicos, estoqueBox);

        card.setOnMouseClicked(event ->{
            Sessao.setTituloSelecionado(titulo);
            navegarPara(event, "/views/bibliotecario/detalheTitulo.fxml");
        });
        return card;
    }

    @FXML private void handleAdicionarLivro(MouseEvent event) { navegarPara(event, "/views/bibliotecario/adicionarLivro.fxml"); }

}
