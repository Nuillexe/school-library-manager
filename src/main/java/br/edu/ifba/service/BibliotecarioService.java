package br.edu.ifba.service;

import br.edu.ifba.models.*;
import br.edu.ifba.repository.PersistenceManager;
import br.edu.ifba.repository.dao.EmprestimoDAOLista;
import br.edu.ifba.repository.dao.LivroDAOLista;
import br.edu.ifba.repository.dao.ReservaDAOFilaDePrioridade;
import br.edu.ifba.repository.dao.ReservaDAOLista;

public class BibliotecarioService {

    private Biblioteca b;

    public BibliotecarioService(Usuario userLogado) {
        this.b = Biblioteca.getInstance();
    }

    public Biblioteca getB() {
        return b;
    }

    // =========================================================================
    // DASHBOARD — Métricas para a tela do Administrador/Bibliotecário
    // =========================================================================

    /** Retorna o total de exemplares físicos cadastrados no acervo global. */
    public int getTotalLivros() {
        return b.getAcervo().quantidade();
    }

    /** Retorna a listagem completa de títulos cadastrados. */
    public Titulo[] obterCatalogo() {
        return b.getTitulos().listar();
    }

    /** Número total de empréstimos armazenados no histórico/lista global. */
    public int getNumeroEmprestimosAtivos() {
        return b.getListaDeEmprestimos().tamanho();
    }

    /**
     * Conta a quantidade de empréstimos atualmente em atraso.
     * Percorre os registros usando o método adaptado com for-each tradicional.
     */
    public int getNumeroEmprestimosAtrasados() {
        int cont = 0;
        for (Emprestimo e : b.getListaDeEmprestimos().listar()) {
            if (e != null && e.isAtrasado()) {
                cont++;
            }
        }
        return cont;
    }

    /**
     * Conta o número de usuários únicos com pelo menos uma pendência por atraso.
     */
    public int getUsuariosComAtraso() {
        int cont = 0;
        for (Usuario u : b.getListaDeUsuarios().listar()) {
            if (u != null && b.getListaDeEmprestimos().usuarioTemAtraso(u)) {
                cont++;
            }
        }
        return cont;
    }

    /**
     * Calcula o somatório de todas as reservas ativas em todas as filas de prioridade.
     */
    public int getTotalReservas() {
        int total = 0;
        for (Titulo t : b.getTitulos().listar()) {
            if (t != null) {
                total += t.getFilaDeReservas().tamanho();
            }
        }
        return total;
    }

    /**
     * Conta a quantidade de empréstimos efetuados na data presente.
     */
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
    // DEVOLUÇÕES — Controle administrativo de devoluções
    // =========================================================================

    /**
     * Registra a devolução de um livro por intermédio da tela do bibliotecário.
     */
    public boolean registrarDevolucao(Emprestimo e) {
        if (e == null) {
            System.out.println("Empréstimo não encontrado ou já encerrado.");
            return false;
        }

        Livro livro = e.getLivro();
        Usuario user = e.getUsuario();

        // Disponibiliza o livro físico novamente
        livro.setDisponivel(true);

        // Desvincula o empréstimo do controle de estoque do Título correspondente
        for (Titulo titulo : b.getTitulos().listar()) {
            if (titulo != null && livro.getIsbn().equalsIgnoreCase(titulo.getIsbn())) {
                titulo.removerEmprestimo(e);

                // Se houver alguém aguardando na fila de prioridade, atende imediatamente
                if (!titulo.getFilaDeReservas().estaVazia()) {
                    atenderPrimeirosDaFila(titulo);
                }
                break;
            }
        }

        // Remove dos registros internos do usuário e do gerenciador global da biblioteca
        user.removerEmprestimo(e);
        b.getListaDeEmprestimos().apagarPorId(e.getId());
        PersistenceManager.sobrescreverEmprestimos(b.getListaDeEmprestimos());

        if (e.isAtrasado()) {
            System.out.println("⚠️ Devolução registrada com atraso para: " + user.getNome());
        } else {
            System.out.println("✅ Devolução registrada com sucesso para: " + user.getNome());
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
        b.getAcervo().ordenar(); // Aplica a ordenação alfabética interna definida no seu DAO
        return b.getAcervo().listar();
    }

    // =========================================================================
    // CONTROLE DE RESERVAS
    // =========================================================================

    /**
     * Captura quem está no topo da fila de reservas de cada título no sistema.
     */
    public Reserva[] listarPrimeirosDasFilaDeReservasDeCadaTitulo() {
        ReservaDAOLista listaAuxiliar = new ReservaDAOLista();
        for (Titulo t : b.getTitulos().listar()) {
            if (t != null) {
                Reserva primeira = t.getFilaDeReservas().proximo(); // Usa o peek() interno da PriorityQueue
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

    /**
     * Aloca o livro diretamente para o próximo usuário prioritário da fila de reservas.
     */
    public boolean atenderPrimeirosDaFila(Titulo titulo) {
        if (titulo == null) return false;

        Reserva proxima = titulo.getFilaDeReservas().proximo();
        if (proxima == null) {
            System.out.println("Fila de reservas vazia para: " + titulo.getNome());
            return false;
        }

        Livro exemplar = titulo.getExemplarDisponivel();
        if (exemplar == null) {
            System.out.println("Nenhum exemplar físico disponível para atender a reserva de: " + titulo.getNome());
            return false;
        }

        // Consome a reserva retirando-a da fila do título e do índice global
        titulo.getFilaDeReservas().removerProximo();
        b.getListaDeReservas().apagar(proxima.getId());

        Usuario beneficiario = proxima.getUsuario();

        // Cria e consolida o novo vínculo de empréstimo
        exemplar.setDisponivel(false);
        Emprestimo emprestimo = new Emprestimo(beneficiario, exemplar);

        beneficiario.adicionarEmprestimo(emprestimo);
        b.getListaDeEmprestimos().salvar(emprestimo);
        titulo.registrarEmprestimo(emprestimo);

        System.out.println("📢 Reserva Atendida! Empréstimo gerado para " + beneficiario.getNome() +
                " — devolução prevista: " + emprestimo.getDataDevolucao());
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
            System.out.println("Livro inválido.");
            return;
        }

        b.getAcervo().salvar(novoLivro);
        PersistenceManager.salvarLivro(novoLivro);
        // Verifica se o ISBN já existe para agrupar o exemplar sob o mesmo Título
        for (Titulo t : b.getTitulos().listar()) {
            if (t != null && novoLivro.getIsbn().equals(t.getIsbn())) {
                correcaoDosDadosDoLivro(novoLivro, t);
                t.addLivro(novoLivro);
                System.out.println("✅ Novo exemplar adicionado ao título '" + t.getNome() + "'");
                return;
            }
        }

        // Se for um ISBN inédito, cria-se uma nova estrutura de Título para gerenciar as futuras filas
        LivroDAOLista novaListaDeExemplares = new LivroDAOLista();
        novaListaDeExemplares.salvar(novoLivro);
        b.getTitulos().salvar(new Titulo(novaListaDeExemplares));
        System.out.println("✅ Novo título cadastrado com sucesso no acervo.");
    }

    /** Sincroniza metadados do exemplar para evitar divergências com o modelo do título */
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
            System.out.println("Exemplar não encontrado.");
            return false;
        }
        System.out.println("✅ Exemplar '" + removido.getNome() + "' removido com sucesso.");
        return true;
    }
}