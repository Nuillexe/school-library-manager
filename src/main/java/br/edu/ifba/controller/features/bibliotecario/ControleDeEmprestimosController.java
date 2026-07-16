package br.edu.ifba.controller.features.bibliotecario;

import br.edu.ifba.models.Emprestimo;
import br.edu.ifba.service.BibliotecarioService;
import br.edu.ifba.util.Sessao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ControleDeEmprestimosController implements Initializable {

    @FXML private FlowPane containerDevolucoes;

    private BibliotecarioService service;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (Sessao.getUsuarioLogado() != null) {
            this.service = new BibliotecarioService(Sessao.getUsuarioLogado());
            renderizarEmprestimos();
        }
    }

    private void renderizarEmprestimos() {
        containerDevolucoes.getChildren().clear();

        // Obtém o array de empréstimos da biblioteca
        Emprestimo[] lista = service.getB().getListaDeEmprestimos().listar();

        if (lista != null) {
            for (Emprestimo emp : lista) {
                if (emp != null) {
                    VBox card = criarCardEmprestimo(emp);
                    containerDevolucoes.getChildren().add(card);
                }
            }
        }
    }

    private VBox criarCardEmprestimo(Emprestimo emp) {
        VBox card = new VBox(15);
        card.setPrefWidth(530);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-border-color: #E0E0E0; -fx-border-radius: 20; -fx-border-width: 1;");

        // Lógica de atraso
        boolean atrasado = LocalDate.now().isAfter(emp.getDataDevolucao());

        // Header: Ícone e Info Principal
        HBox header = new HBox(15);
        VBox iconBox = new VBox(new Label("📖"));
        iconBox.setAlignment(Pos.CENTER);
        iconBox.setMinSize(60, 60);
        iconBox.setStyle(atrasado ? "-fx-background-color: #FEEEEE; -fx-background-radius: 15;" : "-fx-background-color: #F3E5F5; -fx-background-radius: 15;");

        VBox titleBox = new VBox(2);
        Label lblTitulo = new Label(emp.getLivro().getNome());
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        HBox userBox = new HBox(5, new Label("👤"), new Label(emp.getUsuario().getNome()));
        userBox.setAlignment(Pos.CENTER_LEFT);

        Label lblIdExemplar = new Label("ID Livro: " + emp.getLivro().getId());
        lblIdExemplar.setStyle("-fx-text-fill: #888888; -fx-font-size: 13px;");

        titleBox.getChildren().addAll(lblTitulo, userBox, lblIdExemplar);
        header.getChildren().addAll(iconBox, titleBox);

        // Seção de Datas
        VBox dataContainer = new VBox(8);
        dataContainer.setPadding(new Insets(15));
        dataContainer.setStyle("-fx-background-color: #F9F9F9; -fx-background-radius: 10;");

        dataContainer.getChildren().add(criarLinhaData("Data de empréstimo", emp.getDataEmprestimo().format(dtf), false));

        String statusAtraso = atrasado ? " • Atrasado" : "";
        HBox linhaDevolucao = criarLinhaData("Data de devolução", emp.getDataDevolucao().format(dtf) + statusAtraso, atrasado);
        dataContainer.getChildren().add(linhaDevolucao);

        Label lblLog = new Label("ID do Log: " + emp.getId());
        lblLog.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 11px;");
        dataContainer.getChildren().add(lblLog);

        // Botão de Devolução
        Button btnDevolver = new Button("↺ Registrar Devolução");
        btnDevolver.setMaxWidth(Double.MAX_VALUE);
        btnDevolver.setPrefHeight(50);
        btnDevolver.setStyle("-fx-background-color: #5E2A8E; -fx-background-radius: 15; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        btnDevolver.setOnAction(e -> {
            System.out.println("Devolvendo: " + emp.getLivro().getNome());
            service.registrarDevolucao(emp);
            renderizarEmprestimos(); // Atualiza a tela
        });

        card.getChildren().addAll(header, dataContainer, btnDevolver);
        return card;
    }

    private HBox criarLinhaData(String rotulo, String valor, boolean destacar) {
        HBox linha = new HBox();
        Label lblRotulo = new Label(rotulo);
        lblRotulo.setStyle("-fx-text-fill: #777777;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblValor = new Label(valor);
        if (destacar) lblValor.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");

        linha.getChildren().addAll(lblRotulo, spacer, lblValor);
        return linha;
    }
}