package br.edu.ifba.service;

import br.edu.ifba.models.*;
import br.edu.ifba.repository.BibliotecaRepository;
import br.edu.ifba.repository.PersistenceManager;
import br.edu.ifba.repository.dao.LivroDAOLista;
import br.edu.ifba.repository.dao.ReservaDAOLista;
import br.edu.ifba.util.Tools;

public class BibliotecarioService {

    private static BibliotecaRepository b;

    public BibliotecarioService(Usuario userLogado) {
        this.b = BibliotecaRepository.getInstance();
    }

    public BibliotecaRepository getB() {
        return b;
    }

    // =========================================================================
    // DASHBOARD — Métricas para a tela do Administrador/Bibliotecário
    // =========================================================================

    public int getTotalLivros() {
        return b.getAcervo().quantidade();
    }

    public Titulo[] obterCatalogo() {
        return b.getTitulos().listar();
    }

    public int getNumeroEmprestimosAtivos() {
        return b.getListaDeEmprestimos().tamanho();
    }

    public int getNumeroEmprestimosAtrasados() {
        int cont = 0;
        for (Emprestimo e : b.getListaDeEmprestimos().listar()) {
            if (e != null && e.isAtrasado()) {
                cont++;
            }
        }
        return cont;
    }

    public int getUsuariosComAtraso() {
        int cont = 0;
        for (Usuario u : b.getListaDeUsuarios().listar()) {
            if (u != null && b.getListaDeEmprestimos().usuarioTemAtraso(u)) {
                cont++;
            }
        }
        return cont;
    }

    public int getTotalReservas() {
        int total = 0;
        for (Titulo t : b.getTitulos().listar()) {
            if (t != null) {
                total += t.getFilaDeReservas().tamanho();
            }
        }
        return total;
    }

    public int getNumeroEmprestimosHoje() {
        int cont = 0;
        java.time.LocalDate hoje = java.time.LocalDate.now();
        for (Emprestimo e : b.getListaDeEmprestimos().listar()) {
            if (e != null && e.getDataEmprestimo().isEqual(hoje)) {
                cont++;
            }
        }
        return cont;
    }

    // =========================================================================
    // EMPRÉSTIMOS — Controle administrativo de empréstimos
    // =========================================================================

    /**
     * Registra um empréstimo para um usuário (bibliotecário pode emprestar para qualquer um)
     */
    public boolean registrarEmprestimo(Usuario usuario, Livro livro) {
        // 1. Validações básicas
        if (usuario == null || livro == null) {
            Tools.enviarAlerta("❌ Falha no empréstimo: Dados inválidos.");
            return false;
        }

        // 2. Verifica se usuário tem atraso (usando o método do UsuarioService)
        UsuarioService userService = new UsuarioService(usuario);
        if (userService.usuarioPossuiAtraso()) {
            Tools.enviarAlerta("❌ Empréstimo negado: Usuário " + usuario.getNome() + " possui pendências em atraso.");
            return false;
        }

        // 3. Verifica limite de empréstimos (usando getListaEmprestimos().tamanho())
        if (usuario.getListaEmprestimos().tamanho() >= usuario.getLimiteLivros()) {
            Tools.enviarAlerta("❌ Empréstimo negado: Usuário atingiu o limite máximo de " +
                    usuario.getLimiteLivros() + " empréstimos.");
            return false;
        }

        // 4. Verifica se o livro está disponível
        if (!livro.isDisponivel()) {
            Tools.enviarAlerta("❌ Empréstimo negado: Livro \"" + livro.getNome() + "\" indisponível.");
            return false;
        }

        // 5. Processa o empréstimo
        livro.setDisponivel(false);
        Emprestimo emprestimo = new Emprestimo(usuario, livro);

        usuario.adicionarEmprestimo(emprestimo);
        b.getListaDeEmprestimos().salvar(emprestimo);
        PersistenceManager.salvarEmprestimo(emprestimo);
        PersistenceManager.sobrescreverLivros(b.getAcervo());

        // 6. Atualiza o título (se existir)
        for (Titulo t : b.getTitulos().listar()) {
            if (t != null && livro.getIsbn().equals(t.getIsbn())) {
                t.registrarEmprestimo(emprestimo);
                break;
            }
        }

        Tools.enviarAlerta("✅ Empréstimo realizado: " + usuario.getNome() +
                " pegou \"" + livro.getNome() + "\" - Devolução: " +
                emprestimo.getDataDevolucao().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return true;
    }

    // =========================================================================
    // DEVOLUÇÕES — Controle administrativo de devoluções
    // =========================================================================

    public boolean registrarDevolucao(Emprestimo e) {
        if (e == null) {
            Tools.enviarAlerta("❌ Falha: Empréstimo não encontrado ou já encerrado.");
            return false;
        }

        Livro livro = e.getLivro();
        Usuario user = e.getUsuario();

        boolean haviaFila = false;
        String tituloNome = "";

        livro.setDisponivel(true);

        for (Titulo titulo : b.getTitulos().listar()) {
            if (titulo != null && livro.getIsbn().equalsIgnoreCase(titulo.getIsbn())) {
                titulo.removerEmprestimo(e);
                tituloNome = titulo.getNome();

                if (!titulo.getFilaDeReservas().estaVazia()) {
                    haviaFila = true;
                    atenderPrimeirosDaFila(titulo);
                }
                break;
            }
        }

        user.removerEmprestimo(e);
        b.getListaDeEmprestimos().apagarPorId(e.getId());
        PersistenceManager.sobrescreverEmprestimos(b.getListaDeEmprestimos());
        PersistenceManager.sobrescreverLivros(b.getAcervo());

        if (e.isAtrasado()) {
            Tools.enviarAlerta("⚠️ Devolução registrada com atraso para: " + user.getNome());
        } else {
            Tools.enviarAlerta("✅ Devolução registrada com sucesso para: " + user.getNome());
        }

        if (haviaFila) {
            Tools.enviarAlerta("📢 Havia usuários aguardando na fila de reservas. O primeiro foi atendido automaticamente.");
        }

        return true;
    }

    // =========================================================================
    // INVENTÁRIO — Telas de visualização organizada do acervo
    // =========================================================================

    public Titulo[] listarInventario() {
        return b.getTitulos().listar();
    }

    public Livro[] listarExemplares() {
        b.getAcervo().ordenar();
        return b.getAcervo().listar();
    }

    // =========================================================================
    // CONTROLE DE RESERVAS
    // =========================================================================

    public Reserva[] listarPrimeirosDasFilaDeReservasDeCadaTitulo() {
        ReservaDAOLista listaAuxiliar = new ReservaDAOLista();
        for (Titulo t : b.getTitulos().listar()) {
            if (t != null) {
                Reserva primeira = t.getFilaDeReservas().proximo();
                if (primeira != null) {
                    listaAuxiliar.salvar(primeira);
                }
            }
        }
        return listaAuxiliar.listar();
    }

    public Reserva[] listarFilaDeReserva(Titulo t) {
        if (t == null) return new Reserva[0];
        return t.getFilaDeReservas().listar();
    }

    public boolean atenderPrimeirosDaFila(Titulo titulo) {
        if (titulo == null) {
            Tools.enviarAlerta("❌ Erro: Título inválido para atendimento de reserva.");
            return false;
        }

        Reserva proxima = titulo.getFilaDeReservas().proximo();
        if (proxima == null) {
            Tools.enviarAlerta("ℹ️ Fila de reservas vazia para: " + titulo.getNome());
            return false;
        }

        Livro exemplar = titulo.getExemplarDisponivel();
        if (exemplar == null) {
            Tools.enviarAlerta("⚠️ Reserva não atendida: Sem exemplares disponíveis de \"" + titulo.getNome() + "\"");
            return false;
        }

        titulo.getFilaDeReservas().removerProximo();
        b.getListaDeReservas().apagar(proxima.getId());
        PersistenceManager.sobrescreverReservas(b.getListaDeReservas());

        Usuario beneficiario = proxima.getUsuario();

        exemplar.setDisponivel(false);
        Emprestimo emprestimo = new Emprestimo(beneficiario, exemplar);

        beneficiario.adicionarEmprestimo(emprestimo);
        b.getListaDeEmprestimos().salvar(emprestimo);
        titulo.registrarEmprestimo(emprestimo);

        Tools.enviarAlerta("📢 RESERVA ATENDIDA: " + beneficiario.getNome() +
                " retirou \"" + titulo.getNome() + "\" - Devolução: " +
                emprestimo.getDataDevolucao().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return true;
    }

    // =========================================================================
    // GESTÃO DE USUÁRIOS
    // =========================================================================

    public Usuario[] listarUsuarios() {
        return b.getListaDeUsuarios().listar();
    }

    public Usuario buscarUsuarioPorId(String id) {
        return b.getListaDeUsuarios().buscarPorId(id);
    }

    // =========================================================================
    // GESTÃO DO ACERVO (Adicionar / Remover Livros)
    // =========================================================================

    public void adicionarLivro(Livro novoLivro) {
        if (novoLivro == null) {
            Tools.enviarAlerta("❌ Falha: Tentativa de adicionar livro inválido.");
            return;
        }

        b.getAcervo().salvar(novoLivro);
        PersistenceManager.salvarLivro(novoLivro);

        for (Titulo t : b.getTitulos().listar()) {
            if (t != null && novoLivro.getIsbn().equals(t.getIsbn())) {
                correcaoDosDadosDoLivro(novoLivro, t);
                t.addLivro(novoLivro);
                Tools.enviarAlerta("✅ Novo exemplar adicionado ao título '" + t.getNome() + "'");
                return;
            }
        }

        LivroDAOLista novaListaDeExemplares = new LivroDAOLista();
        novaListaDeExemplares.salvar(novoLivro);
        b.getTitulos().salvar(new Titulo(novaListaDeExemplares));
        Tools.enviarAlerta("✅ Novo título cadastrado com sucesso no acervo.");
    }

    private static void correcaoDosDadosDoLivro(Livro l, Titulo t) {
        if (!(l.getAutor().equalsIgnoreCase(t.getAutor()))) l.setAutor(t.getAutor());
        if (!(l.getNome().equalsIgnoreCase(t.getNome()))) l.setNome(t.getNome());
        if (!(l.getGenero().equalsIgnoreCase(t.getGenero()))) l.setGenero(t.getGenero());
        if (!(l.getDescricao().equalsIgnoreCase(t.getDescricao()))) l.setDescricao(t.getDescricao());
        if (!(l.getDataPublicacao().isEqual(t.getDataPublicacao()))) l.setDataPublicacao(t.getDataPublicacao());
    }

    public boolean removerLivro(Long idLivro) {
        Livro removido = b.getAcervo().apagar(idLivro);
        PersistenceManager.sobrescreverLivros(b.getAcervo());
        if (removido == null) {
            Tools.enviarAlerta("❌ Falha: Livro com ID " + idLivro + " não encontrado.");
            return false;
        }
        Tools.enviarAlerta("✅ Livro removido: \"" + removido.getNome() + "\" (ID: " + idLivro + ")");
        return true;
    }

    public static void reinicializarSistema(){
        colocarTodosOsLivrosComoDisponiveis();
        cancelarTodosEmprestimosEReservas();
        PersistenceManager.apagarTodosOsUsuariosCriados();
    }

    private static void colocarTodosOsLivrosComoDisponiveis(){
        for (Livro l : b.getAcervo().listar())
            l.setDisponivel(true);
        PersistenceManager.sobrescreverLivros(b.getAcervo());
    }

    private static void cancelarTodosEmprestimosEReservas(){
        b.getListaDeReservas().limpar();
        PersistenceManager.limparReservas();

        b.getListaDeEmprestimos().limpar();
        PersistenceManager.limparEmprestimos();
    }


}