package br.edu.ifba.controller.features.bibliotecario;

import br.edu.ifba.enums.TipoUsuario;
import br.edu.ifba.models.Reserva;
import br.edu.ifba.models.Titulo;
import br.edu.ifba.models.Usuario;
import br.edu.ifba.service.BibliotecarioService;
import br.edu.ifba.util.Sessao;
import br.edu.ifba.util.Tools;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControleDeReservasController implements Initializable {

    @FXML private Label NomeUsuario;
    @FXML private FlowPane containerFilas;

    private BibliotecarioService service;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Usuario logado = Sessao.getUsuarioLogado();
        if (logado != null) {
            this.NomeUsuario.setText(logado.getNome());
            this.service = new BibliotecarioService(logado);
            renderizarFilasDeReserva();
        }
    }

    private void renderizarFilasDeReserva() {
        containerFilas.getChildren().clear();

        // Percorre todos os títulos para encontrar quem tem fila
        for (Titulo titulo : service.getB().getTitulos().listar()) {
            Reserva[] fila = titulo.getFilaDeReservas().listar();

            if (fila != null && fila.length > 0) {
                VBox cardFila = criarCardFila(titulo, fila);
                containerFilas.getChildren().add(cardFila);
            }
        }
    }

    private VBox criarCardFila(Titulo titulo, Reserva[] fila) {
        VBox card = new VBox(20);
        card.setPrefWidth(560);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 25; -fx-padding: 30; -fx-border-color: #E0E0E0; -fx-border-radius: 25;");

        Label lblTitulo = new Label(titulo.getNome());
        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        ScrollPane scrollInterno = new ScrollPane();
        scrollInterno.setPrefHeight(280);
        scrollInterno.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollInterno.setStyle("-fx-background-color: transparent; -fx-background: white; -fx-border-color: transparent;");

        VBox listaPessoas = new VBox(12);
        listaPessoas.setStyle("-fx-background-color: white;");

        int posicao = 1;
        for (Reserva reserva : fila) {
            if (reserva != null) {
                listaPessoas.getChildren().add(criarItemPessoa(reserva, posicao++));
            }
        }

        scrollInterno.setContent(listaPessoas);
        card.getChildren().addAll(lblTitulo, scrollInterno);
        return card;
    }

    private AnchorPane criarItemPessoa(Reserva reserva, int posicao) {
        AnchorPane item = new AnchorPane();
        item.setPrefHeight(85);
        item.setPrefWidth(475);
        item.setStyle("-fx-background-color: #F8F9FB; -fx-background-radius: 15;");

        VBox info = new VBox(3);
        Label nome = new Label(reserva.getUsuario().getNome());
        nome.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label idUser = new Label("ID: " + reserva.getUsuario().getId());
        idUser.setStyle("-fx-text-fill: #888888; -fx-font-size: 13px;");

        info.getChildren().addAll(nome, idUser);
        AnchorPane.setLeftAnchor(info, 15.0);
        AnchorPane.setTopAnchor(info, 18.0);

        if (reserva.getUsuario().getTipo() == TipoUsuario.PROFESSOR) {
            Label tag = new Label("PRIORIDADE");
            tag.setStyle("-fx-background-color: #FFF9C4; -fx-text-fill: #FBC02D; -fx-padding: 2 8; -fx-background-radius: 5; -fx-font-size: 10px; -fx-font-weight: bold;");
            HBox hBox = new HBox(10, nome, tag);
            hBox.setAlignment(Pos.CENTER_LEFT);
            info.getChildren().set(0, hBox);
        }

        Label lblPos = new Label(posicao + "º");
        lblPos.setAlignment(Pos.CENTER);
        lblPos.setPrefSize(40, 40);
        lblPos.setStyle("-fx-background-color: white; -fx-background-radius: 50; -fx-font-weight: bold; -fx-border-color: #DDD; -fx-border-radius: 50;");
        AnchorPane.setRightAnchor(lblPos, 15.0);
        AnchorPane.setTopAnchor(lblPos, 22.0);

        item.getChildren().addAll(info, lblPos);
        return item;
    }

    // --- Navegação Unificada e Segura ---
    @FXML private void handleLogout(MouseEvent event) {
        Sessao.encerrarSessao();
        navegarPara("/views/AuthViews/login.fxml", event);
    }

    @FXML private void onNavDashboard(MouseEvent event) { navegarPara("/views/bibliotecarioViews/dashboard.fxml", event); }
    @FXML private void onNavInventario(MouseEvent event) { navegarPara("/views/bibliotecarioViews/inventario.fxml", event); }
    @FXML private void onNavReservas(MouseEvent event) { renderizarFilasDeReserva(); }
    @FXML private void onNavEmprestimos(MouseEvent event) { navegarPara("/views/bibliotecarioViews/controleDeEmprestimos.fxml", event); }

    private void navegarPara(String fxmlPath, MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Tools.trocarCenaPreservandoJanela(stage, root);
        } catch (IOException e) {
            System.err.println("Erro ao navegar para " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}