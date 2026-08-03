package br.edu.ifba.controller.features.usuario;

import br.edu.ifba.repository.BibliotecaRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import br.edu.ifba.models.Reserva;
import br.edu.ifba.models.Titulo;
import br.edu.ifba.models.Usuario;
import br.edu.ifba.service.UsuarioService;
import br.edu.ifba.util.Sessao;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ReservasController implements Initializable {

    @FXML private Label lblNomeUsuario;
    @FXML private Label lblContadorReservas;
    @FXML private VBox  emptyStateReservas;
    @FXML private VBox  listaReservasContainer;

    private UsuarioService usuarioService;
    private static final int LIMITE_RESERVAS = 3;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Usuario logado = Sessao.getUsuarioLogado();
        this.usuarioService = new UsuarioService(logado);

        if (logado != null && lblNomeUsuario != null) {
            lblNomeUsuario.setText(logado.getNome());
        }

        carregarReservasReais();
    }

    private void carregarReservasReais() {
        List<Reserva> reservasAtivas = buscarReservasDoUsuario();

        lblContadorReservas.setText(reservasAtivas.size() + "/" + LIMITE_RESERVAS);

        if (reservasAtivas.isEmpty()) {
            mostrarEstadoVazio();
        } else {
            renderizarLista(reservasAtivas);
        }
    }

    private List<Reserva> buscarReservasDoUsuario() {
        List<Reserva> encontradas = new ArrayList<>();
        Usuario logado = Sessao.getUsuarioLogado();

        for (Titulo t : BibliotecaRepository.getInstance().getTitulos().listar()) {
            for (Reserva r : t.getFilaDeReservas().listar()) {
                if (r.getUsuario().getId().equals(logado.getId())) {
                    encontradas.add(r);
                }
            }
        }
        return encontradas;
    }

    private void renderizarLista(List<Reserva> reservas) {
        emptyStateReservas.setVisible(false);
        emptyStateReservas.setManaged(false);
        listaReservasContainer.setVisible(true);
        listaReservasContainer.setManaged(true);

        listaReservasContainer.getChildren().clear();
        for (Reserva r : reservas) {
            listaReservasContainer.getChildren().add(criarCardReserva(r));
        }
    }

    private VBox criarCardReserva(Reserva reserva) {
        VBox card = new VBox(12);
        card.getStyleClass().add("loan-card");

        // --- Cabeçalho do Card ---
        HBox header = new HBox(12);
        VBox iconBox = new VBox(new Label("🕐"));
        iconBox.setStyle("-fx-background-color: #EFF6FF; -fx-background-radius: 8; -fx-padding: 10; -fx-alignment: CENTER;");

        VBox info = new VBox(4);
        HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);

        Label titulo = new Label(reserva.getTitulo().getNome());
        titulo.getStyleClass().add("loan-book-title");

        Label autor = new Label(reserva.getTitulo().getAutor());
        autor.getStyleClass().add("loan-book-id");

        Label badge = new Label("Na fila");
        badge.setStyle("-fx-background-color: #FEF3C7; -fx-background-radius: 20; -fx-padding: 3 12; -fx-text-fill: #92400E; -fx-font-weight: bold;");

        info.getChildren().addAll(titulo, autor, badge);
        header.getChildren().addAll(iconBox, info);

        Separator sep = new Separator();

        VBox detalhes = new VBox(8);

        int posicao = reserva.getTitulo().getFilaDeReservas().posicao(reserva)+1;
        detalhes.getChildren().add(criarLinhaInfo("Posição na fila", posicao + "º"));

        detalhes.getChildren().add(criarLinhaInfo("Status", "Aguardando exemplar"));

        // --- Botão Cancelar ---
        HBox btnRow = new HBox();
        btnRow.setStyle("-fx-alignment: CENTER;");
        Button btnCancelar = new Button("Cancelar Reserva");
        btnCancelar.setStyle("-fx-background-color: transparent; -fx-text-fill: #DC2626; -fx-border-color: #FECACA; -fx-cursor: hand;");

        btnCancelar.setOnAction(e -> {
            if (usuarioService.desistirDaReserva(reserva.getTitulo())) {
                carregarReservasReais(); // Atualiza a tela após cancelar
            }
        });

        btnRow.getChildren().add(btnCancelar);
        card.getChildren().addAll(header, sep, detalhes, btnRow);
        return card;
    }

    private HBox criarLinhaInfo(String label, String valor) {
        HBox row = new HBox();
        Label lbl = new Label(label);
        lbl.getStyleClass().add("loan-label");
        HBox.setHgrow(lbl, javafx.scene.layout.Priority.ALWAYS);
        Label val = new Label(valor);
        val.getStyleClass().add("loan-value");
        row.getChildren().addAll(lbl, val);
        return row;
    }

    private void mostrarEstadoVazio() {
        emptyStateReservas.setVisible(true);
        emptyStateReservas.setManaged(true);
        listaReservasContainer.setVisible(false);
        listaReservasContainer.setManaged(false);
    }
}