package br.edu.ifba.models;

import java.time.LocalDate;
import br.edu.ifba.repository.dao.EmprestimoDAOLista;
import br.edu.ifba.repository.dao.LivroDAOLista;
import br.edu.ifba.repository.dao.filaDeReserva.ReservaDAOFilaDePrioridade;

/**
 * Agrupa dados catalogados e gerencia a coleção física de exemplares, empréstimos e reservas deste título específico.
 */
public class Titulo {

    private String nome;
    private String autor;
    private String isbn;
    private String genero;
    private String descricao;
    private LocalDate dataPublicacao;
    private int quantidade;            // Quantidade total de exemplares cadastrados para este título
    private int quantidadeDeReservas;  // Tamanho atual da fila de espera por este título
    private int quantidadeDisponivel;  // Quantidade de livros físicos livres na estante para empréstimo imediato

    private LivroDAOLista listaDeExemplares;
    private ReservaDAOFilaDePrioridade filaDeReservas; // Fila de prioridade estruturada de acordo com o Tipo do Usuário
    private EmprestimoDAOLista listaDeEmprestimos;


    public Titulo(LivroDAOLista listaDeExemplares){
        // Validação obrigatória: Não faz sentido criar um Título no catálogo sem nenhum exemplar atrelado
        if (listaDeExemplares == null || listaDeExemplares.tamanho() == 0) {
            throw new IllegalArgumentException("Lista de exemplares vazia");
        }

        // Extrai os metadados textuais a partir do primeiro exemplar da coleção modelo
        Livro modelo = listaDeExemplares.selecionar(0);

        this.listaDeExemplares = listaDeExemplares;
        this.listaDeEmprestimos=new EmprestimoDAOLista();
        this.filaDeReservas= new ReservaDAOFilaDePrioridade();

        this.nome = modelo.getNome();
        this.isbn = modelo.getIsbn();
        this.genero = modelo.getGenero();
        this.descricao = modelo.getDescricao();
        this.dataPublicacao = modelo.getDataPublicacao();
        this.autor = modelo.getAutor();

        this.quantidade = listaDeExemplares.tamanho();
        this.quantidadeDeReservas = this.filaDeReservas.tamanho();

        // Calcula a quantidade real de exemplares disponíveis varrendo a lista customizada
        int contadorDisponiveis = 0;
        for (int i = 0; i < listaDeExemplares.tamanho(); i++) {
            Livro l = listaDeExemplares.selecionar(i);
            if (l != null && l.isDisponivel()) {
                contadorDisponiveis++;
            }
        }
        this.quantidadeDisponivel = contadorDisponiveis;

    }


    public Titulo(String isbn) {
        this.isbn = isbn;

        // Todos os campos de texto e objetos fora do parâmetro recebem null
        this.nome = null;
        this.autor = null;
        this.genero = null;
        this.descricao = null;
        this.dataPublicacao = null;
        this.listaDeExemplares = null;
        this.filaDeReservas = null;
        this.listaDeEmprestimos = null;

        // Atributos primitivos numéricos assumem 0
        this.quantidade = 0;
        this.quantidadeDeReservas = 0;
        this.quantidadeDisponivel = 0;
    }

    // Getters e Setters de controle

    public String getNome() { return nome; }
    public String getAutor() { return autor; }
    public String getIsbn() { return isbn; }
    public String getGenero() { return genero; }
    public String getDescricao() { return descricao; }
    public LocalDate getDataPublicacao() { return dataPublicacao; }

    public int getQuantidadeDeExemplares() { return quantidade; }
    public int getQuantidadeDeReservas() { return filaDeReservas.tamanho(); }
    public int getQuantidadeDisponivel() { return quantidadeDisponivel; }

    public LivroDAOLista getListaDeExemplares() { return listaDeExemplares; }
    public ReservaDAOFilaDePrioridade getFilaDeReservas() { return filaDeReservas; }
    public EmprestimoDAOLista getListaDeEmprestimos() { return listaDeEmprestimos; }

    /**
     * Varre a lista de exemplares para localizar e retornar o primeiro objeto físico livre para empréstimo.
     * @return O objeto Livro disponível, ou null se todos estiverem ocupados.
     */
    public Livro getExemplarDisponivel() {
        Livro[] lista = listaDeExemplares.listar();
        for (int i = 0; i < lista.length; i++) {
            if (lista[i] != null && lista[i].isDisponivel()) {
                return lista[i]; // Retorna a referência direta do exemplar físico encontrado
            }
        }
        return null;
    }

    /**
     * Registra o empréstimo de um exemplar diminuindo os contadores internos de estoque.
     */
    public void registrarEmprestimo(Emprestimo novoEmprestimo) {
        if (novoEmprestimo == null || quantidadeDisponivel <= 0) {
            throw new IllegalStateException("Não há exemplares disponíveis");
        }

        listaDeEmprestimos.salvar(novoEmprestimo);
        quantidadeDisponivel--; // Atualiza o controle imediato do estoque na estante
    }

    /**
     * Processa a devolução de um empréstimo específico, liberando o espaço e incrementando o estoque.
     */
    public Emprestimo removerEmprestimo(Emprestimo e) {
        if (e == null) {
            return null;
        }

        // Varre a estrutura interna linear por índice
        for (int i = 0; i < listaDeEmprestimos.tamanho(); i++) {
            Emprestimo emprestimo = listaDeEmprestimos.selecionar(i);

            if (emprestimo != null && e.getId() == emprestimo.getId()) {
                listaDeEmprestimos.remover(i);
                quantidadeDisponivel++; // Incrementa novamente a disponibilidade na estante
                return emprestimo;
            }
        }
        return null;
    }

    /**
     * Insere um novo exemplar físico de forma segura, garantindo que ele pertença ao mesmo código ISBN do Título.
     */
    public void addLivro(Livro l) {
        if (l == null) {
            throw new IllegalArgumentException("O exemplar não pode ser nulo.");
        }

        // Validação de Integridade: O ISBN do exemplar físico deve ser idêntico ao do título catalogado
        if (!l.getIsbn().equalsIgnoreCase(this.getIsbn())) {
            throw new IllegalArgumentException("Este não é um exemplar desse titulo");
        }

        this.listaDeExemplares.salvar(l);
        this.quantidade = this.listaDeExemplares.tamanho(); // Sincroniza a contagem total

        if (l.isDisponivel()) {
            this.quantidadeDisponivel++; // Aumenta o estoque se o livro físico inserido vier como disponível
        }
    }
}