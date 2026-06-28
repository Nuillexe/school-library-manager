package br.edu.ifba.controller.features.usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import br.edu.ifba.models.Titulo;
import br.edu.ifba.models.Usuario;
import br.edu.ifba.service.UsuarioService;
import br.edu.ifba.util.Sessao;
import br.edu.ifba.util.Tools;

public class DetalheLivroController implements Initializable {

    @FXML private Label lblNomeUsuario;
    @FXML private Label lblTitulo;
    @FXML private Label lblAutor;
    @FXML private Label lblAno; // Ano que aparece ao lado do autor
    @FXML private Label lblCategoria;
    @FXML private Label lblIsbn;
    @FXML private Label lblDataPublicacao; // Mantido conforme seu pedido
    @FXML private Label lblDisponibilidade;
    @FXML private Label lblDescricao;
    @FXML private Label lblEmprestimosAtivos;

    @FXML private HBox alertaBloqueado;
    @FXML private Button btnEmprestimo;
    @FXML private Button btnReserva;

    // Adicione estes campos ao topo da classe DetalheLivroController
    @FXML private HBox alertaLimite;
    @FXML private HBox alertaJaPossui;
    @FXML private HBox alertaJaReservado;

    private UsuarioService usuarioService;
    private Titulo tituloAtual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Usuario logado = Sessao.getUsuarioLogado();
        this.usuarioService = new UsuarioService(logado);
        lblNomeUsuario.setText(logado.getNome());

        int ativos = logado.getListaEmprestimos().tamanho();
        int limite = logado.getLimiteLivros();
        lblEmprestimosAtivos.setText("Empréstimos ativos: " + ativos + "/" + limite);
    }

    public void carregarLivro(Titulo titulo) {
        this.tituloAtual = titulo;

        lblTitulo.setText(titulo.getNome());
        lblAutor.setText(titulo.getAutor());

        // Pega a data completa (ex: 2009 ou 10/05/2009)
        String dataCompleta = String.valueOf(titulo.getDataPublicacao());
        lblDataPublicacao.setText(dataCompleta);

        // Se a data for string e você quiser só o ano para o lblAno do topo:
        // lblAno.setText(dataCompleta.length() > 4 ? dataCompleta.substring(dataCompleta.length() - 4) : dataCompleta);
        lblAno.setText(dataCompleta);

        lblCategoria.setText(titulo.getGenero());
        lblIsbn.setText(titulo.getIsbn());
        lblDescricao.setText(titulo.getDescricao());

        int disponivel = titulo.getQuantidadeDisponivel();
        if (disponivel > 0) {
            lblDisponibilidade.setText(disponivel + " exemplar(es) disponível(is)");
            lblDisponibilidade.setStyle("-fx-text-fill: #059669;");
        } else {
            lblDisponibilidade.setText("Indisponível no momento");
            lblDisponibilidade.setStyle("-fx-text-fill: #DC2626;");
        }

        configurarAcoes(disponivel);
    }

    /**
     * Aplica as regras de negócio do UsuarioService para gerenciar os botões
     */
    private void configurarAcoes(int disponivel) {
        // 1. Coleta de dados
        boolean temAtraso = usuarioService.usuarioPossuiAtraso();
        boolean limiteAtingido = usuarioService.atingiuLimiteDeEmprestimos();
        String isbn = tituloAtual.getIsbn();
        boolean jaPossui = usuarioService.esseLivroFoiPegoEmprestado(isbn);
        boolean jaReservou = usuarioService.esseLivroFoiFeitoAReserva(isbn);

        // 2. Reset de visibilidade (Esconde tudo primeiro para limpar a tela)
        alertaBloqueado.setVisible(false); alertaBloqueado.setManaged(false);
        alertaLimite.setVisible(false);    alertaLimite.setManaged(false);
        alertaJaPossui.setVisible(false);  alertaJaPossui.setManaged(false);
        alertaJaReservado.setVisible(false); alertaJaReservado.setManaged(false);
        btnEmprestimo.setVisible(false);   btnEmprestimo.setManaged(false);
        btnReserva.setVisible(false);      btnReserva.setManaged(false);

        // 3. Aplicação das Regras de Prioridade
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
            // Opcional: Se não tiver o livro, mesmo com limite atingido, ele pode querer reservar?
            // Se a regra for "limite atingido não reserva", para aqui.
        }
        else {
            // Se passou em todas as validações, mostramos o botão de ação baseado no estoque
            if (disponivel > 0) {
                exibirElemento(btnEmprestimo);
            } else {
                exibirElemento(btnReserva);
            }
        }
    }

    // Método auxiliar para não repetir código
    private void exibirElemento(javafx.scene.Node node) {
        node.setVisible(true);
        node.setManaged(true);
    }

    @FXML
    private void onEmprestimo() {
        if (usuarioService.pegarEmprestimo(tituloAtual)) {
            // Se deu certo, volta para o catálogo ou atualiza a tela
            System.out.println("✅ Empréstimo realizado!");
            onVoltar();
        }else{
            Tools.enviarAlerta("Não foi possivel concluir o emprestimo");
        }
    }

    @FXML
    private void onReserva() {
        if (usuarioService.fazerReserva(tituloAtual)) {
            System.out.println("✅ Reserva realizada!");
            onVoltar();
        }else{
            Tools.enviarAlerta("Não foi possivel concluir a reserva");
        }
    }

    @FXML private void onVoltar() { navegarPara("/views/usuarioViews/Catalogo.fxml"); }

    @FXML private void onLogout() {
        Sessao.setUsuarioLogado(null); // Acrescentado para limpar a sessão ao deslogar
        navegarPara("/views/AuthViews/login.fxml");
    }

    @FXML private void onNavCatalogo()    { navegarPara("/views/usuarioViews/Catalogo.fxml"); }
    @FXML private void onNavEmprestimos() { navegarPara("/views/usuarioViews/Emprestimos.fxml"); }
    @FXML private void onNavReservas()    { navegarPara("/views/usuarioViews/Reservas.fxml"); }

    // Mantido conforme solicitado (não usa a Tools)
    private void navegarPara(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) lblTitulo.getScene().getWindow();
            Tools.trocarCenaPreservandoJanela(stage, root);
        } catch (IOException e) {
            System.err.println("Erro ao navegar para: " + fxmlPath);
            e.printStackTrace();
        }
    }
}