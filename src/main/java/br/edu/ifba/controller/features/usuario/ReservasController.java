package br.edu.ifba.controller.features.usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import br.edu.ifba.models.Biblioteca;
import br.edu.ifba.models.Reserva;
import br.edu.ifba.models.Titulo;
import br.edu.ifba.models.Usuario;
import br.edu.ifba.service.UsuarioService;
import br.edu.ifba.util.Sessao;
import br.edu.ifba.util.Tools;

import java.io.IOException;
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
        // Inicializa o Service com o usuário da sessão
        Usuario logado = Sessao.getUsuarioLogado();
        this.usuarioService = new UsuarioService(logado);

        // Define o nome visual do usuário
        if (logado != null && lblNomeUsuario != null) {
            lblNomeUsuario.setText(logado.getNome());
        }

        carregarReservasReais();
    }

    /**
     * Busca todas as reservas do usuário percorrendo os títulos da biblioteca
     */
    private void carregarReservasReais() {
        List<Reserva> reservasAtivas = buscarReservasDoUsuario();

        // Atualiza o contador visual (ex: 1/3)
        lblContadorReservas.setText(reservasAtivas.size() + "/" + LIMITE_RESERVAS);

        if (reservasAtivas.isEmpty()) {
            mostrarEstadoVazio();
        } else {
            renderizarLista(reservasAtivas);
        }
    }

    /**
     * Lógica para encontrar as reservas: No seu sistema, elas estão dentro de cada Titulo
     */
    private List<Reserva> buscarReservasDoUsuario() {
        List<Reserva> encontradas = new ArrayList<>();
        Usuario logado = Sessao.getUsuarioLogado();

        // Percorre todos os títulos da biblioteca para achar onde este usuário está na fila
        for (Titulo t : Biblioteca.getInstance().getTitulos().listar()) {
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

        // --- Detalhes da Posição e Data ---
        VBox detalhes = new VBox(8);

        // Posição na fila (calculada pelo DAO do título)
        int pos = reserva.getTitulo().getFilaDeReservas().posicao(reserva)+1;
        detalhes.getChildren().add(criarLinhaInfo("Posição na fila", pos + "º"));

        // Status
        detalhes.getChildren().add(criarLinhaInfo("Status", "Aguardando exemplar"));

        // --- Botão Cancelar ---
        HBox btnRow = new HBox();
        btnRow.setStyle("-fx-alignment: CENTER;");
        Button btnCancelar = new Button("Cancelar Reserva");
        btnCancelar.setStyle("-fx-background-color: transparent; -fx-text-fill: #DC2626; -fx-border-color: #FECACA; -fx-cursor: hand;");

        // Chama o método desistirDaReserva do seu UsuarioService
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

    // ===== NAVEGAÇÃO UTILIZANDO A CLASSE TOOLS (Sua versão oficial) =====

    @FXML
    private void onLogout() {
        Sessao.setUsuarioLogado(null); // Acrescentado para limpar a sessão ao deslogar
        navegarPara("/views/AuthViews/login.fxml");
    }

    @FXML private void onNavCatalogo()    { navegarPara("/views/usuarioViews/Catalogo.fxml"); }
    @FXML private void onNavEmprestimos() { navegarPara("/views/usuarioViews/Emprestimos.fxml"); }
    @FXML private void onNavReservas()    { System.out.println("Já está na página de Reservas"); }

    private void navegarPara(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) lblNomeUsuario.getScene().getWindow();
            Tools.trocarCenaPreservandoJanela(stage, root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}