package br.edu.ifba.controller.features.usuario;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import br.edu.ifba.models.Emprestimo;
import br.edu.ifba.models.Usuario;
import br.edu.ifba.service.UsuarioService;
import br.edu.ifba.util.Sessao;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

public class EmprestimosController implements Initializable {

    @FXML private Label lblNomeUsuario;
    @FXML private HBox  alertaBloqueado;
    @FXML private VBox  listaEmprestimosContainer;
    @FXML private VBox  emptyStateEmprestimos;

    private UsuarioService usuarioService;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Usuario logado = Sessao.getUsuarioLogado();
        this.usuarioService = new UsuarioService(logado);

        if (logado != null && lblNomeUsuario != null) {
            lblNomeUsuario.setText(logado.getNome());
        }

        // Regra de Negócio: Verifica se o usuário tem algum livro atrasado para mostrar o alerta
        boolean temAtraso = usuarioService.usuarioPossuiAtraso();
        alertaBloqueado.setVisible(temAtraso);
        alertaBloqueado.setManaged(temAtraso);

        carregarEmprestimosReais();
    }

    private void carregarEmprestimosReais() {
        Emprestimo[] lista = Sessao.getUsuarioLogado().getListaEmprestimos().listar();

        listaEmprestimosContainer.getChildren().clear();

        if (lista.length == 0) {
            mostrarEstadoVazio(true);
        } else {
            mostrarEstadoVazio(false);
            for (Emprestimo e : lista) {
                listaEmprestimosContainer.getChildren().add(criarCardEmprestimo(e));
            }
        }
    }

    private void mostrarEstadoVazio(boolean vazio) {
        emptyStateEmprestimos.setVisible(vazio);
        emptyStateEmprestimos.setManaged(vazio);
        listaEmprestimosContainer.setVisible(!vazio);
        listaEmprestimosContainer.setManaged(!vazio);
    }

    private VBox criarCardEmprestimo(Emprestimo item) {
        LocalDate hoje = item.getDataEmprestimo();
        LocalDate prevista = item.getDataDevolucao();
        boolean atrasado = item.isAtrasado();

        long diasAtraso = atrasado ? ChronoUnit.DAYS.between(prevista, hoje) : 0;

        VBox card = new VBox(12);
        card.getStyleClass().add(atrasado ? "loan-card-atrasado" : "loan-card");

        // --- Cabeçalho ---
        HBox header = new HBox(12);
        VBox iconBox = new VBox(new Label(atrasado ? "📕" : "📗"));
        iconBox.setStyle(atrasado ? "-fx-background-color: #FEE2E2;" : "-fx-background-color: #EFF6FF;");
        iconBox.getStyleClass().add("loan-icon-box");

        VBox info = new VBox(4);
        HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);

        Label titulo = new Label(item.getLivro().getNome());
        titulo.getStyleClass().add("loan-book-title");

        Label idExemplar = new Label("ID do livro: " + item.getLivro().getId());
        idExemplar.getStyleClass().add("loan-book-id");

        Label badge = new Label(atrasado ? "Atrasado" : "Em dia");
        badge.getStyleClass().add(atrasado ? "tag-atrasado" : "tag-disponivel");

        info.getChildren().addAll(titulo, idExemplar, badge);
        header.getChildren().addAll(iconBox, info);

        // --- Informaçoes ---
        VBox datas = new VBox(8);

        datas.getChildren().add(criarLinhaData("📅 Data de Retirada", hoje.format(FMT)));

        datas.getChildren().add(criarLinhaData("📅 Devolução Prevista", prevista.format(FMT)));

        if (atrasado) {
            Label lblAtraso = new Label("⏰ Atrasado há " + diasAtraso + " dia(s)");
            lblAtraso.getStyleClass().add("loan-atraso-label");
            datas.getChildren().add(lblAtraso);
        }

        card.getChildren().addAll(header, datas);
        return card;
    }

    private HBox criarLinhaData(String label, String valor) {
        HBox row = new HBox();
        Label lbl = new Label(label);
        lbl.getStyleClass().add("loan-label");
        HBox.setHgrow(lbl, javafx.scene.layout.Priority.ALWAYS);
        Label val = new Label(valor);
        val.getStyleClass().add("loan-value");
        row.getChildren().addAll(lbl, val);
        return row;
    }

}