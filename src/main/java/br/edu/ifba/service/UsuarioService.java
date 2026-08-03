package br.edu.ifba.service;

import br.edu.ifba.models.*;
import br.edu.ifba.repository.BibliotecaRepository;
import br.edu.ifba.repository.PersistenceManager;
import br.edu.ifba.util.AlertManager;
import br.edu.ifba.util.NavigationManager;

public class UsuarioService {

    private BibliotecaRepository b;
    private Usuario user;

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
            AlertManager.showError("❌ Falha: Título inválido para empréstimo.");
            return false;
        }

        if (usuarioPossuiAtraso()) {
            AlertManager.alertar("⚠️ Acesso Bloqueado: Regularize suas pendências em atraso antes de realizar empréstimos.");
            return false;
        }

        if (user.getListaEmprestimos().tamanho() >= user.getLimiteLivros()) {
            AlertManager.alertar("⚠️ Limite atingido: Seu plano permite no máximo " + user.getLimiteLivros() + " livros simultâneos.");
            return false;
        }

        if (titulo.getQuantidadeDisponivel() <= 0) {
            AlertManager.showError(("❌ Indisponível: Não há exemplares livres. Sugerimos realizar uma reserva."));
            return false;
        }

        Livro livroFisicoEmprestado = titulo.getExemplarDisponivel();
        if (livroFisicoEmprestado == null) {
            AlertManager.showError("❌ Erro interno: Divergência nos contadores do acervo.");
            return false;
        }

        livroFisicoEmprestado.setDisponivel(false);
        Emprestimo emprestimo = new Emprestimo(user, livroFisicoEmprestado);

        user.getListaEmprestimos().salvar(emprestimo);
        b.getListaDeEmprestimos().salvar(emprestimo);
        titulo.registrarEmprestimo(emprestimo);
        PersistenceManager.salvarEmprestimo(emprestimo);
        PersistenceManager.sobrescreverLivros(b.getAcervo());

        AlertManager.showInfo("✅ Sucesso! Empréstimo realizado. Devolução prevista: " +
                emprestimo.getDataDevolucao().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return true;
    }

    public boolean devolucaoDoEmprestimo(Emprestimo emprestimo) {
        if (emprestimo == null) {
            AlertManager.showError("❌ Falha: Empréstimo inválido ou não localizado.");
            return false;
        }

        Livro livro = emprestimo.getLivro();

        boolean atrasado = java.time.LocalDate.now().isAfter(emprestimo.getDataDevolucao());
        emprestimo.setDataDevolucao(java.time.LocalDate.now()); // Seta data real da devolução
        emprestimo.setAtrasado(atrasado);

        livro.setDisponivel(true);

        // Recalcula estoque no Título correspondente
        Titulo titulo = b.getTitulos().buscarPorIsbn(livro.getIsbn());
        if (titulo != null) {
            titulo.removerEmprestimo(emprestimo);
        }

        user.removerEmprestimo(emprestimo);
        b.getListaDeEmprestimos().apagarPorId(emprestimo.getId());
        PersistenceManager.sobrescreverEmprestimos(b.getListaDeEmprestimos());

        if (atrasado) {
            AlertManager.alertar("⚠️ Livro devolvido com atraso registrado. Regularize pendências.");
        } else {
            AlertManager.showInfo("✅ Livro devolvido com sucesso!");
        }

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
            AlertManager.showError("❌ Falha: Título inválido para reserva.");
            return false;
        }

        if (usuarioPossuiAtraso()) {
           AlertManager.alertar("⚠️ Acesso Bloqueado. Você possui pendências em atraso. Regularize-as antes de reservar.");
            return false;
        }

        // Regra de negócio: Limite máximo estrito de 3 reservas ativas por usuário
        if (contarReservasAtivas() >= 3) {
            AlertManager.alertar("⚠️ Você atingiu a cota máxima permitida de 3 reservas simultâneas.");
            return false;
        }

        Reserva reserva = new Reserva(user, titulo);
        titulo.getFilaDeReservas().salvar(reserva);
        b.getListaDeReservas().salvar(reserva); // Sincroniza no índice global
        PersistenceManager.salvarReserva(reserva);//salva no banco

        AlertManager.showInfo("✅ Reserva efetuada! Você está na posição " +
                (titulo.getFilaDeReservas().posicao(reserva) + 1) + " da fila.");
        return true;
    }

    public boolean desistirDaReserva(Titulo titulo) {
        if (titulo == null) {
            AlertManager.showError("❌ Falha: Título inválido para cancelamento.");
            return false;
        }

        for (Reserva r : titulo.getFilaDeReservas().listar()) {
            if (r != null && r.getUsuario().getId().equals(user.getId())) {
                titulo.getFilaDeReservas().apagar(r.getId());
                b.getListaDeReservas().apagar(r.getId());
                PersistenceManager.sobrescreverReservas(b.getListaDeReservas());//atualiza no banco de dados

                AlertManager.alertar("✅ Reserva cancelada com sucesso.");
                return true;
            }
        }
        AlertManager.alertar("ℹ️ Nenhuma reserva ativa sua foi localizada para este título.");
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
            AlertManager.showInfo("📢 Notificando " + proxima.getUsuario().getNome() +
                    ": o título '" + titulo.getNome() + "' está disponível para retirada!");
        }
    }
}