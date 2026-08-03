package br.edu.ifba.controller.features.usuario;

import br.edu.ifba.util.AlertManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import java.net.URL;
import java.util.ResourceBundle;
import br.edu.ifba.models.Titulo;
import br.edu.ifba.models.Usuario;
import br.edu.ifba.service.UsuarioService;
import br.edu.ifba.util.Sessao;
import br.edu.ifba.util.NavigationManager;

public class DetalheLivroController implements Initializable {

    @FXML private Label lblTitulo;
    @FXML private Label lblAutor;
    @FXML private Label lblAno;
    @FXML private Label lblCategoria;
    @FXML private Label lblIsbn;
    @FXML private Label lblDataPublicacao;
    @FXML private Label lblDisponibilidade;
    @FXML private Label lblDescricao;
    @FXML private Label lblEmprestimosAtivos;

    @FXML private HBox alertaBloqueado;
    @FXML private Button btnEmprestimo;
    @FXML private Button btnReserva;

    @FXML private HBox alertaLimite;
    @FXML private HBox alertaJaPossui;
    @FXML private HBox alertaJaReservado;

    private UsuarioService usuarioService;
    private Titulo tituloAtual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Usuario logado = Sessao.getUsuarioLogado();
        this.usuarioService = new UsuarioService(logado);
        int ativos = logado.getListaEmprestimos().tamanho();
        int limite = logado.getLimiteLivros();
        lblEmprestimosAtivos.setText("Empréstimos ativos: " + ativos + "/" + limite);
        carregarTitulo(Sessao.getTituloSelecionado());
    }

    public void carregarTitulo(Titulo titulo) {
        this.tituloAtual = titulo;

        lblTitulo.setText(titulo.getNome());
        lblAutor.setText(titulo.getAutor());

        String dataCompleta = String.valueOf(titulo.getDataPublicacao());
        lblDataPublicacao.setText(dataCompleta);
        lblAno.setText(dataCompleta);

        lblCategoria.setText(titulo.getGenero());
        lblIsbn.setText(titulo.getIsbn());
        lblDescricao.setText(titulo.getDescricao());

        int exemplaresDisponiveis = titulo.getQuantidadeDisponivel();
        if (exemplaresDisponiveis > 0) {
            lblDisponibilidade.setText(exemplaresDisponiveis + " exemplar(es) disponível(is)");
            lblDisponibilidade.setStyle("-fx-text-fill: #059669;");
        } else {
            lblDisponibilidade.setText("Indisponível no momento");
            lblDisponibilidade.setStyle("-fx-text-fill: #DC2626;");
        }

        configurarAcoes(exemplaresDisponiveis);
    }

    private void configurarAcoes(int exemplaresDisponiveis) {

        boolean temAtraso = usuarioService.usuarioPossuiAtraso();
        boolean limiteAtingido = usuarioService.atingiuLimiteDeEmprestimos();
        String isbn = tituloAtual.getIsbn();
        boolean jaPossui = usuarioService.esseLivroFoiPegoEmprestado(isbn);
        boolean jaReservou = usuarioService.esseLivroFoiFeitoAReserva(isbn);

        alertaBloqueado.setVisible(false); alertaBloqueado.setManaged(false);
        alertaLimite.setVisible(false);    alertaLimite.setManaged(false);
        alertaJaPossui.setVisible(false);  alertaJaPossui.setManaged(false);
        alertaJaReservado.setVisible(false); alertaJaReservado.setManaged(false);
        btnEmprestimo.setVisible(false);   btnEmprestimo.setManaged(false);
        btnReserva.setVisible(false);      btnReserva.setManaged(false);

        // Aplicação das Regras de Prioridade
        if (temAtraso) {
            exibirElemento(alertaBloqueado);
        }
        else if (jaPossui) {
            exibirElemento(alertaJaPossui);
        }
        else if (jaReservou) {
            exibirElemento(alertaJaReservado);
        }
        else if (limiteAtingido) {
            exibirElemento(alertaLimite);
        }
        else {
            if (exemplaresDisponiveis > 0) {
                exibirElemento(btnEmprestimo);
            } else {
                exibirElemento(btnReserva);
            }
        }
    }

    private void exibirElemento(javafx.scene.Node node) {
        node.setVisible(true);
        node.setManaged(true);
    }

    @FXML
    private void onEmprestimo() {
        if (usuarioService.pegarEmprestimo(tituloAtual)) {
            AlertManager.showInfo("Emprestimo realizado com sucesso");
            System.out.println("✅ Empréstimo realizado!");
            onVoltar();
        }else{
            AlertManager.alertar("Não foi possivel concluir o emprestimo");
        }
    }

    @FXML
    private void onReserva() {
        if (usuarioService.fazerReserva(tituloAtual)) {
            System.out.println("✅ Reserva realizada!");
            AlertManager.showInfo("✅ Reserva realizada!");
            onVoltar();
        }else{
            AlertManager.alertar("Não foi possivel concluir a reserva");
        }
    }

    @FXML private void onVoltar() { NavigationManager.navegarPara( lblTitulo,"/views/usuario/Catalogo.fxml"); }


}