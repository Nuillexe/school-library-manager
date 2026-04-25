package service;

import model.*;
import repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class BibliotecarioService {

    private final Biblioteca biblioteca;
    private final LivroDaoLista listaDeLivros;
    private final TituloDaoLista listaDeTitulos;
    private final EmprestimoDaoLista listaDeEmprestimos;
    private final UsuarioDAOLista listaDeUsuarios;
    private final Usuario user; // bibliotecário logado

    public BibliotecarioService(Biblioteca biblioteca, Usuario userLogado) {
        this.biblioteca = biblioteca;
        this.listaDeLivros = biblioteca.acervo;
        this.listaDeTitulos = biblioteca.listaDeTitulos;
        this.listaDeEmprestimos = biblioteca.listaDeEmprestimos;
        this.listaDeUsuarios = biblioteca.listaDeUsuarios;
        this.user = userLogado;
    }

    // =========================================================================
    // DASHBOARD — dados gerais (tela do bibliotecário)
    // =========================================================================

    /** Total de exemplares (livros) no acervo. */
    public int getTotalLivros() {
        return listaDeLivros.getQuantidade();
    }

    /** Número de empréstimos ativos (ainda não devolvidos). */
    public int getEmprestimosAtivos() {
        return (int) listaDeEmprestimos.getLista().stream()
                .filter(e -> e.getDataDevolucao() == null)
                .count();
    }

    /** Número de empréstimos atrasados. */
    public int getEmprestimosAtrasados() {
        return (int) listaDeEmprestimos.getLista().stream()
                .filter(Emprestimo::isAtrasada)
                .count();
    }

    /** Número de usuários com pelo menos um empréstimo atrasado. */
    public int getUsuariosComAtraso() {
        return (int) listaDeUsuarios.getLista().stream()
                .filter(u -> possuiAtraso(u))
                .count();
    }

    /** Total de reservas ativas em todas as filas. */
    public int getTotalReservas() {
        return listaDeTitulos.getLista().stream()
                .mapToInt(t -> t.getFilaDeReservas().tamanho())
                .sum();
    }

    // =========================================================================
    // DEVOLUÇÕES — tela de controle de devoluções
    // =========================================================================

    /**
     * Lista todos os empréstimos (ativos e concluídos) com seus dados completos.
     */
    public List<Emprestimo> listarTodosEmprestimos() {
        return listaDeEmprestimos.getLista();
    }

    /**
     * Registra manualmente a devolução de um empréstimo identificado pelo ID do livro
     * e pelo ID do usuário responsável.
     *
     * @param idEmprestimo ID do empréstimo a ser encerrado
     * @return true se a devolução foi registrada; false se não encontrado
     */
    public boolean registrarDevolucao(String idEmprestimo) {
        Emprestimo emprestimo = listaDeEmprestimos.getLista().stream()
                .filter(e -> e.getId().equals(idEmprestimo) && e.getDataDevolucao() == null)
                .findFirst()
                .orElse(null);

        if (emprestimo == null) {
            System.out.println("Empréstimo não encontrado ou já encerrado.");
            return false;
        }

        LocalDate hoje = LocalDate.now();
        emprestimo.setDataDevolucao(hoje);
        emprestimo.setAtrasada(hoje.isAfter(emprestimo.getDataDevolucaoPrevista()));

        Livro livro = emprestimo.getLivro();
        livro.setEstaDisponivel(true);

        // Atualiza contadores do título
        Titulo titulo = listaDeTitulos.buscarTitulo(livro.getNome());
        if (titulo != null) {
            titulo.setQuantidadeDisponivel(titulo.getQuantidadeDisponivel() + 1);
        }

        // Remove o livro da lista do usuário responsável
        listaDeUsuarios.getLista().stream()
                .filter(u -> u.getListaEmprestimos().contains(livro))
                .findFirst()
                .ifPresent(u -> u.devolverLivro(livro));

        // Notifica o próximo da fila de reservas
        if (titulo != null) {
            Reserva proxima = titulo.getFilaDeReservas().peek();
            if (proxima != null) {
                System.out.println("Notificando " + proxima.getUsuario().getNome() +
                        ": o livro '" + titulo.getNome() + "' está disponível!");
            }
        }

        System.out.println("Devolução registrada com sucesso." +
                (emprestimo.isAtrasada() ? " (empréstimo estava atrasado)" : ""));
        return true;
    }

    // =========================================================================
    // INVENTÁRIO — tela de inventário
    // =========================================================================

    /**
     * Lista todos os títulos do acervo, ordenados por nome.
     */
    public List<Titulo> listarInventario() {
        listaDeTitulos.ordenarLista();
        return listaDeTitulos.getLista();
    }

    /**
     * Lista todos os exemplares (livros) do acervo, ordenados por nome.
     */
    public List<Livro> listarExemplares() {
        listaDeLivros.ordenarLista();
        return listaDeLivros.getLista();
    }

    // =========================================================================
    // CONTROLE DE RESERVAS — tela de controle de reservas
    // =========================================================================

    /**
     * Retorna, para cada título, o primeiro usuário da fila de reservas.
     * Estrutura de retorno: lista de títulos que possuem pelo menos uma reserva,
     * com o primeiro usuário da fila acessível via titulo.getFilaDeReservas().peek().
     */
    public List<Titulo> listarPrimeirosDaFilaDeReservas() {
        return listaDeTitulos.getLista().stream()
                .filter(t -> t.getFilaDeReservas().tamanho() > 0)
                .collect(Collectors.toList());
    }

    /**
     * Efetua o empréstimo para o primeiro da fila de reservas de um título,
     * quando um exemplar se torna disponível.
     *
     * @param titulo o título cujo primeiro da fila será atendido
     * @return true se o empréstimo foi gerado; false se a fila estiver vazia
     *         ou não houver exemplar disponível
     */
    public boolean atenderPrimeirosDaFila(Titulo titulo) {
        Reserva proxima = titulo.getFilaDeReservas().peek();
        if (proxima == null) {
            System.out.println("Fila de reservas vazia para: " + titulo.getNome());
            return false;
        }

        // Busca um exemplar disponível
        Livro exemplar = listaDeLivros.getLista().stream()
                .filter(l -> l.getNome().equalsIgnoreCase(titulo.getNome()) && l.isEstaDisponivel())
                .findFirst()
                .orElse(null);

        if (exemplar == null) {
            System.out.println("Nenhum exemplar disponível de: " + titulo.getNome());
            return false;
        }

        // Remove da fila e realiza o empréstimo
        titulo.getFilaDeReservas().desenfileirar();
        Usuario beneficiario = proxima.getUsuario();

        LocalDate hoje = LocalDate.now();
        LocalDate dataDevolucaoPrevista = hoje.plusDays(prazoEmDias(beneficiario));
        Emprestimo emprestimo = new Emprestimo(gerarIdEmprestimo(), exemplar, hoje, dataDevolucaoPrevista);

        exemplar.setEstaDisponivel(false);
        beneficiario.pegarLivro(exemplar);
        listaDeEmprestimos.adicionar(emprestimo);
        titulo.setQuantidadeDisponivel(titulo.getQuantidadeDisponivel() - 1);

        System.out.println("Empréstimo gerado para " + beneficiario.getNome() +
                " — devolução prevista: " + dataDevolucaoPrevista);
        return true;
    }

    // =========================================================================
    // GESTÃO DE USUÁRIOS
    // =========================================================================

    /** Retorna a lista completa de usuários. */
    public List<Usuario> listarUsuarios() {
        return listaDeUsuarios.getLista();
    }

    /** Busca um usuário pelo ID. */
    public Usuario buscarUsuarioPorId(String id) {
        return listaDeUsuarios.getLista().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // =========================================================================
    // GESTÃO DO ACERVO
    // =========================================================================

    /**
     * Adiciona um novo exemplar (livro) ao acervo e atualiza o título correspondente.
     *
     * @param livro o exemplar a ser adicionado
     */
    public void adicionarLivro(Livro livro) {
        listaDeLivros.adicionar(livro);

        Titulo titulo = listaDeTitulos.buscarTitulo(livro.getNome());
        if (titulo == null) {
            // Cria um novo título se ainda não existir
            titulo = new Titulo(
                    livro.getNome(),
                    livro.getAutor(),
                    livro.getDataDePublicacao(),
                    livro.getIsbn(),
                    livro.getGenero(),
                    livro.getDescricao()
            );
            titulo.setQuantidade(1);
            titulo.setQuantidadeDisponivel(1);
            listaDeTitulos.adicionar(titulo);
        } else {
            titulo.setQuantidade(titulo.getQuantidade() + 1);
            if (livro.isEstaDisponivel()) {
                titulo.setQuantidadeDisponivel(titulo.getQuantidadeDisponivel() + 1);
            }
        }

        System.out.println("Livro '" + livro.getNome() + "' adicionado ao acervo.");
    }

    /**
     * Remove um exemplar do acervo pelo ID.
     *
     * @param idLivro ID do exemplar a ser removido
     * @return true se removido; false se não encontrado
     */
    public boolean removerLivro(String idLivro) {
        Livro livro = listaDeLivros.getLista().stream()
                .filter(l -> l.getId().equals(idLivro))
                .findFirst()
                .orElse(null);

        if (livro == null) {
            System.out.println("Exemplar não encontrado.");
            return false;
        }

        listaDeLivros.remover(livro);

        Titulo titulo = listaDeTitulos.buscarTitulo(livro.getNome());
        if (titulo != null) {
            titulo.setQuantidade(titulo.getQuantidade() - 1);
            if (livro.isEstaDisponivel()) {
                titulo.setQuantidadeDisponivel(titulo.getQuantidadeDisponivel() - 1);
            }
            if (titulo.getQuantidade() <= 0) {
                listaDeTitulos.remover(titulo);
            }
        }

        System.out.println("Exemplar removido com sucesso.");
        return true;
    }

    // =========================================================================
    // Helpers privados
    // =========================================================================

    private boolean possuiAtraso(Usuario u) {
        return listaDeEmprestimos.getLista().stream()
                .anyMatch(e -> e.getLivro() != null
                        && u.getListaEmprestimos().contains(e.getLivro())
                        && e.isAtrasada());
    }

    private int prazoEmDias(Usuario u) {
        return switch (u.getCategoria()) {
            case Professor -> 14;
            case Bibliotecario -> 7;
            default -> 7;
        };
    }

    private String gerarIdEmprestimo() {
        return "EMP-" + System.currentTimeMillis();
    }
}