package br.edu.ifba.service;

import br.edu.ifba.models.*;
import br.edu.ifba.repository.BibliotecaRepository;
import br.edu.ifba.repository.PersistenceManager;
import br.edu.ifba.repository.dao.EmprestimoDAOLista;
import br.edu.ifba.util.Tools;

public class UsuarioService {

    private BibliotecaRepository b;
    private Usuario user; // Representa o usuário logado na sessão do aplicativo

    public UsuarioService(Usuario userLogado) {
        this.b = BibliotecaRepository.getInstance();
        this.user = userLogado;
    }

    // =========================================================================
    // FLUXO DE EMPRÉSTIMO
    // =========================================================================

    /**
     * Valida os critérios e concede o empréstimo de um livro para o usuário comum.
     */
    public boolean pegarEmprestimo(Titulo titulo) {
        if (titulo == null) {
            Tools.enviarAlerta("❌ Falha: Título inválido para empréstimo.");
            return false;
        }

        // 1. Bloqueio por atraso pendente
        if (usuarioPossuiAtraso()) {
            Tools.enviarAlerta("⚠️ Acesso Bloqueado: Regularize suas pendências em atraso antes de realizar empréstimos.");
            return false;
        }

        // 2. Bloqueio por estouro de cota do plano (Aluno=3, Professor=4, Bibliotecário=5)
        if (user.getListaEmprestimos().tamanho() >= user.getLimiteLivros()) {
            Tools.enviarAlerta("⚠️ Limite atingido: Seu plano permite no máximo " + user.getLimiteLivros() + " livros simultâneos.");
            return false;
        }

        // 3. Verifica disponibilidade de estoque no contador do título
        if (titulo.getQuantidadeDisponivel() <= 0) {
            Tools.enviarAlerta("❌ Indisponível: Não há exemplares livres. Sugerimos realizar uma reserva.");
            return false;
        }

        // 4. Captura a referência física do exemplar disponível
        Livro livroFisicoEmprestado = titulo.getExemplarDisponivel();
        if (livroFisicoEmprestado == null) {
            Tools.enviarAlerta("❌ Erro interno: Divergência nos contadores do acervo.");
            return false;
        }

        // 5. Consolidação e persistência em memória
        livroFisicoEmprestado.setDisponivel(false);
        Emprestimo emprestimo = new Emprestimo(user, livroFisicoEmprestado);

        user.getListaEmprestimos().salvar(emprestimo); // Aloca no perfil do usuário
        b.getListaDeEmprestimos().salvar(emprestimo); // Grava no registro central
        titulo.registrarEmprestimo(emprestimo);       // Registra no Título
        PersistenceManager.salvarEmprestimo(emprestimo);// Registra no banco de dados
        PersistenceManager.sobrescreverLivros(b.getAcervo());

        Tools.enviarAlerta("✅ Sucesso! Empréstimo realizado. Devolução prevista: " +
                emprestimo.getDataDevolucao().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return true;
    }

    /**
     * Realiza a devolução de um livro por intermédio da sessão do Usuário.
     */
    public boolean devolucaoDoEmprestimo(Emprestimo emprestimo) {
        if (emprestimo == null) {
            Tools.enviarAlerta("❌ Falha: Empréstimo inválido ou não localizado.");
            return false;
        }

        Livro livro = emprestimo.getLivro();

        // Processa cálculo de atrasos em tempo de execução
        boolean atrasado = java.time.LocalDate.now().isAfter(emprestimo.getDataDevolucao());
        emprestimo.setDataDevolucao(java.time.LocalDate.now()); // Seta data real da devolução
        emprestimo.setAtrasado(atrasado);

        // Libera o livro físico
        livro.setDisponivel(true);

        // Recalcula estoque no Título correspondente
        Titulo titulo = b.getTitulos().buscarPorIsbn(livro.getIsbn());
        if (titulo != null) {
            titulo.removerEmprestimo(emprestimo);
        }

        // Limpeza essencial para liberar as cotas do plano
        user.removerEmprestimo(emprestimo);
        b.getListaDeEmprestimos().apagarPorId(emprestimo.getId());
        PersistenceManager.sobrescreverEmprestimos(b.getListaDeEmprestimos());

        if (atrasado) {
            Tools.enviarAlerta("⚠️ Livro devolvido com atraso registrado. Regularize pendências.");
        } else {
            Tools.enviarAlerta("✅ Livro devolvido com sucesso!");
        }

        // Dispara o alerta caso haja um próximo usuário na fila de prioridade
        notificarProximoDaFila(titulo);
        return true;
    }

    // =========================================================================
    // FILTROS DO CATÁLOGO
    // =========================================================================

    public Titulo[] obterCatalogo() {
        return b.getTitulos().listar();
    }

    public Titulo[] buscarTituloPorNome(String busca) {
        if (busca == null || busca.isBlank()) {
            return obterCatalogo();
        }
        Titulo[] todos = b.getTitulos().listar();

        int contador = 0;
        for (Titulo t : todos) {
            if (t != null && t.getNome().toLowerCase().contains(busca.toLowerCase())) {
                contador++;
            }
        }

        Titulo[] resultado = new Titulo[contador];
        int i = 0;
        for (Titulo t : todos) {
            if (t != null && t.getNome().toLowerCase().contains(busca.toLowerCase())) {
                resultado[i++] = t;
            }
        }
        return resultado;
    }

    public Titulo[] filtrarPorGenero(String genero) {
        if (genero == null || genero.isBlank()) {
            return obterCatalogo();
        }
        return b.getTitulos().buscarPorGenero(genero);
    }

    // =========================================================================
    // SISTEMA DE RESERVAS
    // =========================================================================

    public boolean fazerReserva(Titulo titulo) {
        if (titulo == null) {
            Tools.enviarAlerta("❌ Falha: Título inválido para reserva.");
            return false;
        }

        if (usuarioPossuiAtraso()) {
            Tools.enviarAlerta("⚠️ Acesso Bloqueado. Você possui pendências em atraso. Regularize-as antes de reservar.");
            return false;
        }

        // Regra de negócio: Limite máximo estrito de 3 reservas ativas por usuário
        if (contarReservasAtivas() >= 3) {
            Tools.enviarAlerta("⚠️ Você atingiu a cota máxima permitida de 3 reservas simultâneas.");
            return false;
        }

        Reserva reserva = new Reserva(user, titulo);
        titulo.getFilaDeReservas().salvar(reserva);
        b.getListaDeReservas().salvar(reserva); // Sincroniza no índice global
        PersistenceManager.salvarReserva(reserva);//salva no banco

        Tools.enviarAlerta("✅ Reserva efetuada! Você está na posição " +
                (titulo.getFilaDeReservas().posicao(reserva) + 1) + " da fila.");
        return true;
    }

    public boolean desistirDaReserva(Titulo titulo) {
        if (titulo == null) {
            Tools.enviarAlerta("❌ Falha: Título inválido para cancelamento.");
            return false;
        }

        for (Reserva r : titulo.getFilaDeReservas().listar()) {
            if (r != null && r.getUsuario().getId().equals(user.getId())) {
                titulo.getFilaDeReservas().apagar(r.getId());
                b.getListaDeReservas().apagar(r.getId());
                PersistenceManager.sobrescreverReservas(b.getListaDeReservas());//atualiza no banco de dados

                Tools.enviarAlerta("✅ Reserva cancelada com sucesso.");
                return true;
            }
        }
        Tools.enviarAlerta("ℹ️ Nenhuma reserva ativa sua foi localizada para este título.");
        return false;
    }

    // =========================================================================
    // CHECAGEM DE STATUS E COMPATIBILIDADE DA UI
    // =========================================================================

    public boolean usuarioPossuiAtraso() {
        return b.getListaDeEmprestimos().usuarioTemAtraso(user);
    }

    public boolean atingiuLimiteDeEmprestimos() {
        return user.getListaEmprestimos().tamanho() >= user.getLimiteLivros();
    }

    public boolean esseLivroFoiPegoEmprestado(String isbn) {
        for (Emprestimo e : user.getListaEmprestimos().listar()) {
            if (e != null && e.getLivro().getIsbn().equals(isbn)) {
                return true;
            }
        }
        return false;
    }

    public boolean esseLivroFoiFeitoAReserva(String isbn) {
        for (Reserva r : b.getListaDeReservas().listar()) {
            if (r != null && r.getUsuario().getId().equals(user.getId()) && r.getTitulo().getIsbn().equals(isbn)) {
                return true;
            }
        }
        return false;
    }

    // =========================================================================
    // Helpers privados
    // =========================================================================

    /** Varre os títulos contabilizando quantas reservas pertencem ao usuário logado */
    private int contarReservasAtivas() {
        int cont = 0;
        for (Titulo t : b.getTitulos().listar()) {
            if (t != null) {
                for (Reserva r : t.getFilaDeReservas().listar()) {
                    if (r != null && r.getUsuario().getId().equals(user.getId())) {
                        cont++;
                    }
                }
            }
        }
        return cont;
    }

    private void notificarProximoDaFila(Titulo titulo) {
        if (titulo == null) return;

        Reserva proxima = titulo.getFilaDeReservas().proximo();
        if (proxima != null) {
            Tools.enviarAlerta("📢 Notificando " + proxima.getUsuario().getNome() +
                    ": o título '" + titulo.getNome() + "' está disponível para retirada!");
        }
    }
}