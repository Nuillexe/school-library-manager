package service;

import model.*;
import repository.*;

import java.time.LocalDate;
import java.util.List;

public class UsuarioService {

    private final Biblioteca biblioteca;
    private final LivroDaoLista acervo;          // lista de livros (exemplares) da biblioteca
    private final TituloDaoLista listaDeTitulos;
    private final Usuario user;

    public UsuarioService(Biblioteca biblioteca, Usuario userLogado) {
        this.biblioteca = biblioteca;
        this.acervo = biblioteca.acervo;
        this.listaDeTitulos = biblioteca.listaDeTitulos;
        this.user = userLogado;
    }

    // =========================================================================
    // EMPRÉSTIMO
    // =========================================================================

    /**
     * Tenta emprestar um exemplar do livro solicitado ao usuário logado.
     *
     * Regras verificadas (na ordem da tela do livro do PDF):
     *  1. Usuário não pode ter devoluções em atraso.
     *  2. Usuário não pode ter atingido o limite de empréstimos.
     *  3. Deve existir pelo menos um exemplar disponível do livro.
     *
     * @param livro o exemplar a ser emprestado
     * @return true se o empréstimo foi efetuado; false caso contrário
     */
    public boolean pegarEmprestimo(Livro livro) {
        // 1. Verificar atraso
        if (usuarioPossuiAtraso()) {
            System.out.println("⚠️ Acesso Bloqueado. Você possui livros em atraso. " +
                    "Regularize suas devoluções para fazer novos empréstimos.");
            return false;
        }

        // 2. Verificar limite de empréstimos
        if (user.getListaEmprestimos().size() >= user.getLimiteLivros()) {
            System.out.println("Você já pegou o máximo de livros que poderia ter pego.");
            return false;
        }

        // 3. Verificar disponibilidade do exemplar
        if (!livro.isEstaDisponivel()) {
            System.out.println("Este exemplar não está disponível no momento.");
            return false;
        }

        // Efetuar o empréstimo
        LocalDate hoje = LocalDate.now();
        LocalDate dataDevolucaoPrevista = hoje.plusDays(prazoEmDias());

        Emprestimo emprestimo = new Emprestimo(gerarIdEmprestimo(), livro, hoje, dataDevolucaoPrevista);

        livro.setEstaDisponivel(false);
        user.pegarLivro(livro);
        biblioteca.listaDeEmprestimos.adicionar(emprestimo);

        // Atualiza contadores do título correspondente
        Titulo titulo = listaDeTitulos.buscarTitulo(livro.getNome());
        if (titulo != null) {
            titulo.setQuantidadeDisponivel(titulo.getQuantidadeDisponivel() - 1);
        }

        System.out.println("Empréstimo realizado com sucesso! Devolução prevista: " + dataDevolucaoPrevista);
        return true;
    }

    /**
     * Registra a devolução de um exemplar emprestado ao usuário logado.
     *
     * @param livro o exemplar a ser devolvido
     * @return true se a devolução foi registrada; false se o usuário não
     *         possui este livro em sua lista de empréstimos
     */
    public boolean devolucaoDoEmprestimo(Livro livro) {
        // Localiza o empréstimo ativo deste usuário para este livro
        Emprestimo emprestimo = encontrarEmprestimoAtivo(livro);
        if (emprestimo == null) {
            System.out.println("Nenhum empréstimo ativo encontrado para este livro.");
            return false;
        }

        // Marca a devolução
        LocalDate hoje = LocalDate.now();
        emprestimo.setDataDevolucao(hoje);
        emprestimo.setAtrasada(hoje.isAfter(emprestimo.getDataDevolucaoPrevista()));

        livro.setEstaDisponivel(true);
        user.devolverLivro(livro);

        // Atualiza contadores do título correspondente
        Titulo titulo = listaDeTitulos.buscarTitulo(livro.getNome());
        if (titulo != null) {
            titulo.setQuantidadeDisponivel(titulo.getQuantidadeDisponivel() + 1);
        }

        if (emprestimo.isAtrasada()) {
            System.out.println("Livro devolvido com atraso. Por favor, regularize pendências futuras.");
        } else {
            System.out.println("Livro devolvido com sucesso!");
        }

        // Notifica o próximo da fila de reservas (se houver)
        notificarProximoDaFila(titulo);

        return true;
    }

    // =========================================================================
    // CATÁLOGO
    // =========================================================================

    /**
     * Retorna todos os títulos do catálogo (sem filtro).
     */
    public List<Titulo> mostrarCatalogo() {
        listaDeTitulos.ordenarLista();
        return listaDeTitulos.getLista();
    }

    /**
     * Busca títulos cujo nome contenha o texto informado (case-insensitive).
     * Prioridade: se o texto não for vazio, filtra por nome; caso contrário,
     * delega para filtrarPorGenero.
     *
     * @param busca texto a procurar no nome do título
     * @return lista filtrada de títulos
     */
    public List<Titulo> buscarTituloPorNome(String busca) {
        if (busca == null || busca.isBlank()) {
            return mostrarCatalogo();
        }
        return listaDeTitulos.getLista().stream()
                .filter(t -> t.getNome().toLowerCase().contains(busca.toLowerCase()))
                .toList();
    }

    /**
     * Filtra títulos por gênero.
     *
     * @param genero gênero desejado; se nulo ou vazio, retorna tudo
     * @return lista filtrada de títulos
     */
    public List<Titulo> filtrarPorGenero(String genero) {
        if (genero == null || genero.isBlank()) {
            return mostrarCatalogo();
        }
        return listaDeTitulos.selecionaTituloPorGenero(genero);
    }

    // =========================================================================
    // RESERVAS
    // =========================================================================

    /**
     * Faz uma reserva para o usuário logado no título informado.
     * O usuário pode ter no máximo 3 reservas simultâneas.
     *
     * @param titulo o título desejado
     * @return true se a reserva foi efetuada; false caso contrário
     */
    public boolean fazerReserva(Titulo titulo) {
        if (usuarioPossuiAtraso()) {
            System.out.println("⚠️ Acesso Bloqueado. Você possui livros em atraso. " +
                    "Regularize suas devoluções para fazer novos empréstimos.");
            return false;
        }

        long reservasAtivas = contarReservasAtivas();
        if (reservasAtivas >= 3) {
            System.out.println("Você já atingiu o limite de 3 reservas simultâneas.");
            return false;
        }

        Reserva reserva = new Reserva(user);
        titulo.getFilaDeReservas().enfileirar(reserva);

        System.out.println("Reserva realizada! Você está na posição " +
                titulo.getFilaDeReservas().posicao(reserva) + " da fila.");
        return true;
    }

    /**
     * Cancela a reserva do usuário logado no título informado.
     *
     * @param titulo o título cuja reserva será cancelada
     * @return true se cancelada; false se não havia reserva
     */
    public boolean desistirDaReserva(Titulo titulo) {
        boolean removida = titulo.getFilaDeReservas().remover(user);
        if (removida) {
            System.out.println("Reserva cancelada com sucesso.");
        } else {
            System.out.println("Nenhuma reserva encontrada para este título.");
        }
        return removida;
    }

    // =========================================================================
    // STATUS DO USUÁRIO
    // =========================================================================

    /**
     * Verifica se o usuário logado possui algum empréstimo com devolução atrasada.
     */
    public boolean usuarioPossuiAtraso() {
        return biblioteca.listaDeEmprestimos.getLista().stream()
                .anyMatch(e -> e.getLivro() != null
                        && user.getListaEmprestimos().contains(e.getLivro())
                        && e.isAtrasada());
    }

    /**
     * Verifica se o usuário atingiu o limite de empréstimos.
     */
    public boolean atingiuLimiteDeEmprestimos() {
        return user.getListaEmprestimos().size() >= user.getLimiteLivros();
    }

    // =========================================================================
    // Helpers privados
    // =========================================================================

    private Emprestimo encontrarEmprestimoAtivo(Livro livro) {
        return biblioteca.listaDeEmprestimos.getLista().stream()
                .filter(e -> e.getLivro().getId().equals(livro.getId())
                        && e.getDataDevolucao() == null) // ainda não devolvido
                .findFirst()
                .orElse(null);
    }

    private long contarReservasAtivas() {
        return listaDeTitulos.getLista().stream()
                .flatMap(t -> t.getFilaDeReservas().stream())
                .filter(r -> r.getUsuario().getId().equals(user.getId()))
                .count();
    }

    private void notificarProximoDaFila(Titulo titulo) {
        if (titulo == null) return;
        Reserva proxima = titulo.getFilaDeReservas().peek();
        if (proxima != null) {
            System.out.println("Notificando " + proxima.getUsuario().getNome() +
                    ": o livro '" + titulo.getNome() + "' está disponível para retirada!");
        }
    }

    /** Retorna o prazo de empréstimo em dias conforme a categoria do usuário. */
    private int prazoEmDias() {
        return switch (user.getCategoria()) {
            case Professor -> 14;
            case Bibliotecario -> 7;
            default -> 7; // Aluno
        };
    }

    private String gerarIdEmprestimo() {
        return "EMP-" + System.currentTimeMillis();
    }
}