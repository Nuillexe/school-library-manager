package br.edu.ifba.controller.features.bibliotecario;

import br.edu.ifba.models.Reserva;
import br.edu.ifba.models.Usuario;
import br.edu.ifba.service.BibliotecarioService;
import br.edu.ifba.util.AlertManager;
import br.edu.ifba.util.Sessao;
import br.edu.ifba.util.NavigationManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.stage.Window;

public class DashboardController implements Initializable {

    @FXML private Label lblQtdAcervo, lblQtdEmprestimos, lblQtdHoje,
            lblQtdAtrasos, lblQtdReservas, lblQtdUsuariosAtraso, lblFilaContagem;

    @FXML private ListView<Reserva> lvFilaReserva;

    @FXML private BorderPane mainContainer;

    @FXML private Button resetBtn;

    private BibliotecarioService service;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Usuario logado = Sessao.getUsuarioLogado();

        if (logado != null) {

            this.service = new BibliotecarioService(logado);
            carregarDadosDashboard();
            configurarListaDeReservas();
        }
    }

    private void carregarDadosDashboard() {
        try {
            if (lblQtdAcervo != null) lblQtdAcervo.setText(String.valueOf(service.getTotalLivros()));
            if (lblQtdEmprestimos != null) lblQtdEmprestimos.setText(String.valueOf(service.getNumeroEmprestimosAtivos()));
            if (lblQtdAtrasos != null) lblQtdAtrasos.setText(String.valueOf(service.getNumeroEmprestimosAtrasados()));
            if (lblQtdHoje != null) lblQtdHoje.setText(String.valueOf(service.getNumeroEmprestimosHoje()));
            if (lblFilaContagem != null) lblFilaContagem.setText("(" + service.getTotalReservas() + ")");

            // Proteções preventivas contra NullPointerException (IDs ausentes no FXML fornecido)
            if (lblQtdReservas != null) lblQtdReservas.setText(String.valueOf(service.getTotalReservas()));
            if (lblQtdUsuariosAtraso != null) lblQtdUsuariosAtraso.setText(String.valueOf(service.getUsuariosComAtraso()));
        } catch (Exception e) {
            System.err.println("Erro ao carregar dados do Service: " + e.getMessage());
        }
    }

    private void configurarListaDeReservas() {
        ObservableList<Reserva> reservas = FXCollections.observableArrayList(service.listarPrimeirosDasFilaDeReservasDeCadaTitulo());
        lvFilaReserva.setItems(reservas);

        lvFilaReserva.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Reserva reserva, boolean empty) {
                super.updateItem(reserva, empty);
                if (empty || reserva == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(reserva.getUsuario().getNome() + " — " + reserva.getTitulo().getNome());
                    setStyle("-fx-background-color: #F8F9FB; -fx-padding: 8; -fx-background-radius: 5; -fx-margin: 2;");
                }
            }
        });
    }

    @FXML
    private void reinicializarOSistema(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Reinicialização do sistema");
        alert.setHeaderText("Tipos de reinicialização do sistema");
        alert.setContentText("""
            Reinicialização Parcial: Todas as reservas e empréstimos serão cancelados e todos os livros passarão a estar disponíveis no sistema.
            
            Reinicialização para Testes: Os dados do sistema serão todos apagados e ele será povoado com os dados de demonstração presentes em data/seed. Ideal para testes e demonstrações.
            
            Reinicialização Total: APAGA TODOS OS DADOS, exceto o cadastro do usuário do tipo bibliotecario que esta logado e os ids de demonstração.
            """);

        ButtonType parcial = new ButtonType("Reinicialização Parcial");
        ButtonType testes = new ButtonType("Reinicialização para Testes");
        ButtonType total = new ButtonType("Reinicialização Total");

        alert.getButtonTypes().addAll(parcial, testes, total);

        // Força a janela do Alert a aceitar o clique do "X" e fechar
        Window window = alert.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(e -> alert.hide());

        Optional<ButtonType> resposta = alert.showAndWait();

        if (resposta.isPresent()) {
            ButtonType opcaoSelecionada = resposta.get();
            System.out.println("Opção escolhida: " + opcaoSelecionada.getText());

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Ação");
            confirmacao.setHeaderText("Atenção! Esta ação modificará os dados do sistema.");
            confirmacao.setContentText("Você tem certeza de que deseja executar a " + opcaoSelecionada.getText() + "?");

            Optional<ButtonType> confirmou = confirmacao.showAndWait();

            if (confirmou.isPresent() && confirmou.get() == ButtonType.OK) {
                if (opcaoSelecionada == parcial) {
                    BibliotecarioService.reinicializacaoParcial();
                } else if (opcaoSelecionada == testes) {
                    BibliotecarioService.reinicializacaoParaTestes();
                } else if (opcaoSelecionada == total) {
                    BibliotecarioService.reinicializacaoTotal();
                }
                NavigationManager.navegarPara(event, "/views/bibliotecario/dashboard.fxml");
                carregarDadosDashboard();
            } else {
                System.out.println("Operação cancelada na tela de confirmação.");
            }
        } else {
            System.out.println("Reinicialização cancelada pelo usuário (clicou no X).");
        }
    }


    @FXML
    private void handleLogout() {
        Sessao.encerrarSessao();
        NavigationManager.navegarPara(mainContainer,"/views/bibliotecario/dashboard.fxml");
    }

}