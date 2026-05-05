package com.biblioqueue.controller;

import com.biblioqueue.util.Navegador;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class DetalheLivroController implements Initializable {

    @FXML private Label lblNomeUsuario;
    @FXML private Label lblTitulo;
    @FXML private Label lblAutor;
    @FXML private Label lblAno;
    @FXML private Label lblCategoria;
    @FXML private Label lblIsbn;
    @FXML private Label lblIdExemplar;
    @FXML private Label lblAnoPublicacao;
    @FXML private Label lblDisponibilidade;
    @FXML private Label lblDescricao;
    @FXML private Label lblEmprestimosAtivos;

    @FXML private HBox alertaBloqueado;
    @FXML private Button btnEmprestimo;
    @FXML private Button btnReserva;

    private boolean acessoBloqueado   = true;
    private int     emprestimosAtivos = 2;
    private int     limiteEmprestimos = 3;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblNomeUsuario.setText("Ana Silva");
        lblEmprestimosAtivos.setText("Empréstimos ativos: " + emprestimosAtivos + "/" + limiteEmprestimos);
    }

    public void carregarLivro(String titulo, String autor, String ano, String categoria, int disponivel) {
        lblTitulo.setText(titulo);
        lblAutor.setText(autor);
        lblAno.setText(ano);
        lblAnoPublicacao.setText(ano);
        lblCategoria.setText(categoria);
        lblIsbn.setText("978-0262033848");
        lblIdExemplar.setText("#COMP-001");
        lblDescricao.setText("Referência completa sobre " + titulo.toLowerCase() + ".");

        if (disponivel > 0) {
            lblDisponibilidade.setText(disponivel + " exemplar(es)");
            lblDisponibilidade.getStyleClass().setAll("detail-field-value-green");
        } else {
            lblDisponibilidade.setText("Indisponível");
            lblDisponibilidade.getStyleClass().setAll("loan-value-atrasado");
        }

        atualizarBotaoAcao(disponivel);
    }

    private void atualizarBotaoAcao(int disponivel) {
        if (acessoBloqueado) {
            alertaBloqueado.setVisible(true);  alertaBloqueado.setManaged(true);
            btnEmprestimo.setVisible(false);   btnEmprestimo.setManaged(false);
            btnReserva.setVisible(false);      btnReserva.setManaged(false);
        } else if (disponivel > 0 && emprestimosAtivos < limiteEmprestimos) {
            alertaBloqueado.setVisible(false); alertaBloqueado.setManaged(false);
            btnEmprestimo.setVisible(true);    btnEmprestimo.setManaged(true);
            btnReserva.setVisible(false);      btnReserva.setManaged(false);
        } else {
            alertaBloqueado.setVisible(false); alertaBloqueado.setManaged(false);
            btnEmprestimo.setVisible(false);   btnEmprestimo.setManaged(false);
            btnReserva.setVisible(true);       btnReserva.setManaged(true);
        }
    }

    @FXML private void onVoltar()         { navegar("/fxml/Catalogo.fxml"); }
    @FXML private void onEmprestimo()     { System.out.println("Empréstimo: " + lblTitulo.getText()); }
    @FXML private void onReserva()        { System.out.println("Reserva: " + lblTitulo.getText()); }
    @FXML private void onLogout()         { navegar("/fxml/Login.fxml"); }
    @FXML private void onNavCatalogo()    { navegar("/fxml/Catalogo.fxml"); }
    @FXML private void onNavEmprestimos() { navegar("/fxml/Emprestimos.fxml"); }
    @FXML private void onNavReservas()    { navegar("/fxml/Reservas.fxml"); }

    private void navegar(String fxml) {
        Navegador.ir((Stage) lblTitulo.getScene().getWindow(), fxml);
    }
}
