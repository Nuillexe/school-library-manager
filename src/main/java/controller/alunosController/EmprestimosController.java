package com.biblioqueue.controller;

import com.biblioqueue.util.Navegador;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

public class EmprestimosController implements Initializable {

    @FXML private Label lblNomeUsuario;
    @FXML private HBox  alertaBloqueado;
    @FXML private VBox  listaEmprestimosContainer;
    @FXML private VBox  emptyStateEmprestimos;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final List<EmprestimoItem> emprestimos = List.of(
        new EmprestimoItem("Estruturas de Dados e Algoritmos", "#COMP-001",
                LocalDate.of(2026, 4, 7), LocalDate.of(2026, 4, 14)),
        new EmprestimoItem("Engenharia de Software", "#COMP-003",
                LocalDate.of(2026, 4, 9), LocalDate.of(2026, 4, 16))
    );

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblNomeUsuario.setText("Ana Silva");

        boolean temAtraso = emprestimos.stream()
                .anyMatch(e -> e.dataPrevista.isBefore(LocalDate.now()));
        alertaBloqueado.setVisible(temAtraso);
        alertaBloqueado.setManaged(temAtraso);

        if (emprestimos.isEmpty()) {
            emptyStateEmprestimos.setVisible(true);
            emptyStateEmprestimos.setManaged(true);
        } else {
            for (int i = 2; i < emprestimos.size(); i++)
                listaEmprestimosContainer.getChildren().add(criarCardEmprestimo(emprestimos.get(i)));
        }
    }

    private VBox criarCardEmprestimo(EmprestimoItem item) {
        boolean atrasado = item.dataPrevista.isBefore(LocalDate.now());
        long diasAtraso  = atrasado ? ChronoUnit.DAYS.between(item.dataPrevista, LocalDate.now()) : 0;

        VBox card = new VBox(12);
        card.getStyleClass().add(atrasado ? "loan-card-atrasado" : "loan-card");

        HBox header = new HBox(12);
        VBox iconBox = new VBox();
        iconBox.setStyle(atrasado
                ? "-fx-background-color: #FEE2E2; -fx-background-radius: 8; -fx-padding: 10; -fx-min-width: 42px; -fx-min-height: 42px; -fx-max-width: 42px; -fx-max-height: 42px;"
                : "-fx-background-color: #EFF6FF; -fx-background-radius: 8; -fx-padding: 10; -fx-min-width: 42px; -fx-min-height: 42px; -fx-max-width: 42px; -fx-max-height: 42px;");
        Label iconLabel = new Label(atrasado ? "📕" : "📗");
        iconLabel.setStyle("-fx-font-size: 18px;");
        iconBox.getChildren().add(iconLabel);

        VBox info = new VBox(4);
        HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);
        Label titulo = new Label(item.titulo);
        titulo.getStyleClass().add("loan-book-title");
        Label idExemplar = new Label(item.idExemplar);
        idExemplar.getStyleClass().add("loan-book-id");
        HBox badgeRow = new HBox();
        Label badge = new Label(atrasado ? "Atrasado" : "Em dia");
        badge.getStyleClass().add(atrasado ? "tag-atrasado" : "tag-disponivel");
        badgeRow.getChildren().add(badge);
        info.getChildren().addAll(titulo, idExemplar, badgeRow);
        header.getChildren().addAll(iconBox, info);

        VBox datas = new VBox(8);
        HBox rowRetirada = new HBox();
        Label lblR = new Label("📅 Data de Retirada");
        lblR.getStyleClass().add("loan-label");
        HBox.setHgrow(lblR, javafx.scene.layout.Priority.ALWAYS);
        Label valR = new Label(item.dataRetirada.format(FMT));
        valR.getStyleClass().add("loan-value");
        rowRetirada.getChildren().addAll(lblR, valR);

        HBox rowPrevista = new HBox();
        Label lblP = new Label("📅 Data Prevista");
        lblP.getStyleClass().add("loan-label");
        HBox.setHgrow(lblP, javafx.scene.layout.Priority.ALWAYS);
        Label valP = new Label(item.dataPrevista.format(FMT));
        valP.getStyleClass().add(atrasado ? "loan-value-atrasado" : "loan-value");
        rowPrevista.getChildren().addAll(lblP, valP);
        datas.getChildren().addAll(rowRetirada, rowPrevista);

        if (atrasado) {
            HBox rowAtraso = new HBox();
            Label lblA = new Label("⏰ Atrasado há " + diasAtraso + " dia(s)");
            lblA.getStyleClass().add("loan-atraso-label");
            rowAtraso.getChildren().add(lblA);
            datas.getChildren().add(rowAtraso);
        }

        card.getChildren().addAll(header, datas);
        return card;
    }

    @FXML private void onLogout()         { navegar("/fxml/Login.fxml"); }
    @FXML private void onNavCatalogo()    { navegar("/fxml/Catalogo.fxml"); }
    @FXML private void onNavEmprestimos() { }
    @FXML private void onNavReservas()    { navegar("/fxml/Reservas.fxml"); }

    private void navegar(String fxml) {
        Navegador.ir((Stage) lblNomeUsuario.getScene().getWindow(), fxml);
    }

    public record EmprestimoItem(String titulo, String idExemplar, LocalDate dataRetirada, LocalDate dataPrevista) {}
}
