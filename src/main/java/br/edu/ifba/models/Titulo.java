package br.edu.ifba.models;

import java.time.LocalDate;
import java.util.Arrays;

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
        if (listaDeExemplares == null || listaDeExemplares.size() == 0) {
            throw new IllegalArgumentException("Lista de exemplares vazia");
        }

        // Extrai os metadados textuais a partir do primeiro exemplar da coleção modelo
        Livro modelo = listaDeExemplares.get(0);

        this.listaDeExemplares = listaDeExemplares;
        this.listaDeEmprestimos=new EmprestimoDAOLista();
        this.filaDeReservas= new ReservaDAOFilaDePrioridade();

        this.nome = modelo.getNome();
        this.isbn = modelo.getIsbn();
        this.genero = modelo.getGenero();
        this.descricao = modelo.getDescricao();
        this.dataPublicacao = modelo.getDataPublicacao();
        this.autor = modelo.getAutor();

        this.quantidade = listaDeExemplares.size();
        this.quantidadeDeReservas = this.filaDeReservas.tamanho();
        // Calcula a quantidade real de exemplares disponíveis varrendo a lista customizada
        int contadorDisponiveis = 0;
        for (int i = 0; i < listaDeExemplares.size(); i++) {
            Livro l = listaDeExemplares.get(i);
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

    // --- Getters ---
    public String getNome() {
        return nome;
    }
    public String getAutor() {
        return autor;
    }
    public String getIsbn() {
        return isbn;
    }
    public String getGenero() {
        return genero;
    }
    public String getDescricao() {
        return descricao;
    }
    public LocalDate getDataPublicacao() {
        return dataPublicacao;
    }
    public int getQuantidadeDeExemplares() {
        return quantidade;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    private int contarQuantidadeDisponivel(){
        int cont = 0;
        // Percorre os exemplares que este título possui
        for (Livro l : listaDeExemplares.listar()) {
            if (l.isDisponivel()) { // Verifica o booleano do livro físico
                cont++;
            }
        }
        return cont;
    }

    public ReservaDAOFilaDePrioridade getFilaDeReservas() {
        System.out.println(Arrays.toString(filaDeReservas.listar()));
        System.out.println("-----------");
        return filaDeReservas;
    }

    public EmprestimoDAOLista getListaDeEmprestimos() {
        return listaDeEmprestimos;
    }

    public LivroDAOLista getListaDeExemplares() {
        return listaDeExemplares;
    }

    // Retorna um exemplar disponível
    public Livro getExemplarDisponivel(){

        Livro[] lista = listaDeExemplares.listar().toArray(new Livro[0]);

        // Percorre todos os exemplares
        for(int i = 0; i < lista.length; i++){

            if(lista[i] != null && lista[i].isDisponivel()){
                return lista[i];
            }
        }

        return null;
    }

    // Retornar exception caso quantidade disponvel seja null
    public void registrarEmprestimo(Emprestimo novoEmprestimo){
        listaDeEmprestimos.salvar(novoEmprestimo);

        quantidadeDisponivel--;
    }

    public Emprestimo removerEmprestimo(Emprestimo e){

        if(e == null){
            return null;
        }

        // Percorre a lista de empréstimos
        for(int i = 0; i < listaDeEmprestimos.tamanho(); i++){

            Emprestimo emprestimo = listaDeEmprestimos.selecionar(i);

            if(emprestimo != null && e.getId() == emprestimo.getId()){

                listaDeEmprestimos.remover(i);

                quantidadeDisponivel++;

                return emprestimo;
            }
        }

        return null;
    }

    public void addLivro(Livro l){
        if(l==null){
            throw new IllegalArgumentException();
        }

        if(!l.getIsbn().equalsIgnoreCase(this.getIsbn())){
            throw new IllegalArgumentException("Este não é um exemplar desse titulo");
        }

        this.listaDeExemplares.salvar(l);
        quantidade=this.listaDeExemplares.size();
        quantidadeDisponivel++;

    }
}