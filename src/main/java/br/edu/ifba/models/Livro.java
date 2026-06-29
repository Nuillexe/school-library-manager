package br.edu.ifba.models;

import java.time.LocalDate;

/**
 * Classe que representa um exemplar de um livro físico no acervo da biblioteca.
 */
public class Livro {

    private Long id;
    private static long idCount = 0; // Contador estático para geração de IDs automáticos e sequenciais
    private String nome;
    private String autor;
    private String isbn;
    private String genero;
    private String descricao;
    private LocalDate dataPublicacao;
    private boolean disponivel;

    /**
     * Construtor completo da classe Livro.
     * Ao ser instanciado, o livro recebe um ID único incremental e seu status inicial é 'disponível'.
     */
    public Livro(String nome, String autor, String isbn, String genero,
                 String descricao, LocalDate dataPublicacao) {

        this.id = ++idCount; // Incrementa e atribui o ID único de forma automática
        this.nome = nome;
        this.autor = autor;
        this.isbn = isbn;
        this.genero = genero;
        this.descricao = descricao;
        this.dataPublicacao = dataPublicacao;

        // Todo exemplar recém-criado inicia como disponível para empréstimo
        this.disponivel = true;
    }

    public Livro(long id, String nome, String autor, String isbn,
                 String genero, String descricao,
                 LocalDate dataPublicacao, boolean disponivel) {

        this.id = id;

        if (id > idCount) {
            idCount = id;
        }

        this.nome = nome;
        this.autor = autor;
        this.isbn = isbn;
        this.genero = genero;
        this.descricao = descricao;
        this.dataPublicacao = dataPublicacao;
        this.disponivel = disponivel;
    }




    public Livro(Long id) {
        this.id = id;
        this.disponivel = true;
        this.nome = null;
        this.autor = null;
        this.isbn = null;
        this.genero = null;
        this.descricao = null;
        this.dataPublicacao = null;
    }
    // Métodos Getters e Setters com comentários descritivos

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(LocalDate dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    /**
     * Verifica se o exemplar físico específico está livre para ser emprestado.
     */
    public boolean isDisponivel() {
        return disponivel;
    }

    /**
     * Altera o status de disponibilidade do exemplar (ex: mudar para false quando emprestado).
     */
    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    /**
     * Compara dois objetos Livro com base em seus identificadores únicos (id).
     */
    @Override
    public boolean equals(Object obj) {
        // Se for o exato mesmo espaço de memória, são iguais
        if (this == obj) {
            return true;
        }

        // Se o objeto for nulo ou pertencer a outra classe, não são iguais
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Livro outro = (Livro) obj;

        // Caso o ID de um deles seja nulo, não podemos realizar a validação com segurança
        if (this.id == null) {
            return false;
        }

        // Considera dois livros iguais apenas se possuírem o mesmo ID numérico
        return this.id.equals(outro.id);
    }

    /**
     * Retorna uma representação legível do objeto Livro em formato de texto.
     */
    @Override
    public String toString() {
        return "Livro{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", autor='" + autor + '\'' +
                ", isbn='" + isbn + '\'' +
                ", genero='" + genero + '\'' +
                ", disponivel=" + disponivel +
                '}';
    }
}