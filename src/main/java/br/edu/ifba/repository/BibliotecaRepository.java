package br.edu.ifba.repository;

import br.edu.ifba.models.Emprestimo;
import br.edu.ifba.models.Livro;
import br.edu.ifba.models.Reserva;
import br.edu.ifba.models.Titulo;
import br.edu.ifba.repository.dao.*;

import java.util.ArrayList;

public class BibliotecaRepository {
    private LivroDAOLista acervo;
    private TituloDAOLista listaDeTitulos;
    private EmprestimoDAOLista listaDeEmprestimos;
    private ReservaDAOLista listaDeReservas;
    private UsuarioDAOLista listaDeUsuarios;
    private ArrayList<String> listaDeIds;
    private static BibliotecaRepository instance;

    private BibliotecaRepository() {
        // Inicializa as listas vazias
        this.acervo = PersistenceManager.carregarLivros();
        this.listaDeUsuarios = PersistenceManager.carregarUsuarios();
        this.listaDeEmprestimos = PersistenceManager.carregarEmprestimos();
        this.listaDeReservas = PersistenceManager.carregarReservas();
        this.listaDeIds=PersistenceManager.carregarIds();

        if(acervo!=null){
            // Gera os títulos baseados no acervo populado
            this.listaDeTitulos = updateListaDeTitulos(this.acervo);
        }else{
            listaDeTitulos=new TituloDAOLista();
        }
    }

    public static BibliotecaRepository getInstance() {
        if (instance == null) {
            instance = new BibliotecaRepository();
        }
        return instance;
    }

    public boolean thisIDIsValid(String id) {
        if (id == null || id.isEmpty()) return false;

        for (String validId : listaDeIds) {
            if (validId.equals(id)) return true;
        }
        return false;
    }

    public LivroDAOLista getAcervo() {
        return acervo;
    }

    public EmprestimoDAOLista getListaDeEmprestimos() {
        return listaDeEmprestimos;
    }

    public ReservaDAOLista getListaDeReservas() {
        return listaDeReservas;
    }

    public UsuarioDAOLista getListaDeUsuarios() {
        return listaDeUsuarios;
    }

    public TituloDAOLista getTitulos(){
        return this.listaDeTitulos;
    }

    /*public TituloDAOLista getTitulosAtualizados() {
        // Toda vez que alguém pedir os títulos, você re-agrupa para garantir
        // que novos livros adicionados ao acervo apareçam aqui.
        this.listaDeTitulos = updateListaDeTitulos(this.acervo);
        return this.listaDeTitulos;
    }*/

    private TituloDAOLista updateListaDeTitulos(LivroDAOLista acervo) {
        acervo.ordenar();
        TituloDAOLista novaListaDeTitulos = new TituloDAOLista();

        int i = 0;
        while (i < acervo.quantidade()) {
            Livro modelo = acervo.selecionar(i);
            String isbnAtual = modelo.getIsbn();
            LivroDAOLista colecaoExemplares = new LivroDAOLista();

            while (i < acervo.quantidade() && acervo.selecionar(i).getIsbn().equals(isbnAtual)) {
                colecaoExemplares.salvar(acervo.selecionar(i));
                i++;
            }


            // Filtramos as listas globais para pegar apenas o que é deste ISBN
            EmprestimoDAOLista emprestimosFiltrados = filtrarEmprestimosPorIsbn(isbnAtual);
            ReservaDAOFilaDePrioridade reservasFiltradas = filtrarReservasPorIsbn(isbnAtual);

            // Criamos o título com as suas respectivas listas já vindo da persistência global
            novaListaDeTitulos.salvar(new Titulo(colecaoExemplares, emprestimosFiltrados, reservasFiltradas));
        }
        return novaListaDeTitulos;
    }

    // Métodos auxiliares dentro da Biblioteca para ajudar no filtro:
    private EmprestimoDAOLista filtrarEmprestimosPorIsbn(String isbn) {
        EmprestimoDAOLista filtrada = new EmprestimoDAOLista();
        for (Emprestimo e : this.listaDeEmprestimos.listar()) {
            if (e.getLivro().getIsbn().equals(isbn)) {
                filtrada.salvar(e);
            }
        }
        return filtrada;
    }

    private ReservaDAOFilaDePrioridade filtrarReservasPorIsbn(String isbn) {
        ReservaDAOFilaDePrioridade filtrada = new ReservaDAOFilaDePrioridade();
        for (Reserva r : this.listaDeReservas.listar()) {
            if (r.getTitulo().getIsbn().equals(isbn)) {
                filtrada.salvar(r);
            }
        }
        return filtrada;
    }
    public int contarTotalEmprestimos() {
        return listaDeEmprestimos.tamanho();
    }

    public int contarTotalReservas() {
        return listaDeReservas.tamanho();
    }

    public  Livro removerLivro(long idDoLivro){
        Livro l=this.acervo.apagar(idDoLivro);
        if(l==null){
            return null;
        }else{
            listaDeTitulos.buscarPorNome(l.getNome()).getListaDeExemplares().apagar(idDoLivro);
            PersistenceManager.sobrescreverLivros(this.acervo);

        }
        return l;
    }



}
