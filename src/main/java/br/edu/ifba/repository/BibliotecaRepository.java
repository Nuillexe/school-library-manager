package br.edu.ifba.repository;

import br.edu.ifba.models.*;
import br.edu.ifba.repository.dao.*;
import br.edu.ifba.repository.dao.filaDeReserva.ReservaDAOFilaDePrioridade;

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
        this.acervo = PersistenceManager.carregarLivros();
        this.listaDeUsuarios = PersistenceManager.carregarUsuarios();
        this.listaDeEmprestimos = PersistenceManager.carregarEmprestimos();
        this.listaDeReservas = PersistenceManager.carregarReservas();
        this.listaDeIds=PersistenceManager.carregarIds();

        ajustarRelacionamentos();
    }

    public static BibliotecaRepository getInstance() {
        if (instance == null) {
            instance = new BibliotecaRepository();
        }
        return instance;
    }


    public void ajustarRelacionamentos(){
        if(acervo.size()!=0){
            relacionandoEmprestimoELivro();
            relacionandoUsuariosEEmprestimos();
            relacionandoUsuariosEReservas();

            this.listaDeTitulos = updateListaDeTitulos(this.acervo);

            relacionandoTituloEEmprestimos();
            relacionandoTituloEReservas();
        }
    }


    private TituloDAOLista updateListaDeTitulos(LivroDAOLista acervo) {
        acervo.ordenar();
        TituloDAOLista novaListaDeTitulos = new TituloDAOLista();

        int i = 0;
        while (i < acervo.size()) {
            Livro modelo = acervo.get(i);
            String isbnAtual = modelo.getIsbn();
            LivroDAOLista colecaoExemplares = new LivroDAOLista();

            while (i < acervo.size() && acervo.get(i).getIsbn().equals(isbnAtual)) {
                colecaoExemplares.salvar(acervo.get(i));
                i++;
            }


            novaListaDeTitulos.salvar(new Titulo(colecaoExemplares));
        }
        return novaListaDeTitulos;
    }

    private void relacionandoUsuariosEEmprestimos(){
        for (Usuario u: this.listaDeUsuarios.listar()){
            for(Emprestimo e:  this.listaDeEmprestimos.listar()){
                if(e.getUsuario().getId().equalsIgnoreCase(u.getId())){
                    e.setUsuario(u);
                    u.adicionarEmprestimo(e);
                }
            }
        }
    }

    private void relacionandoUsuariosEReservas(){
        for (Usuario u: this.listaDeUsuarios.listar()){
            for(Reserva r:  this.listaDeReservas.listar()){
                if(r.getUsuario().getId().equalsIgnoreCase(u.getId())){
                    r.setUsuario(u);
                }
            }
        }
    }

    private void relacionandoEmprestimoELivro(){
        for(Emprestimo e: listaDeEmprestimos.listar()){
            for(Livro l: acervo.listar()){
                if (e.getLivro().getId()==l.getId()){
                    e.setLivro(l);
                }
            }
        }
    }


    private void relacionandoTituloEEmprestimos(){
        for(Emprestimo e: listaDeEmprestimos.listar()){
            for(Titulo t: listaDeTitulos.listar()){
                if(e.getLivro().getIsbn().equalsIgnoreCase(t.getIsbn())){
                    t.getListaDeEmprestimos().salvar(e);
                }
            }
        }
    }

    private void relacionandoTituloEReservas(){
        for(Reserva r: listaDeReservas.listar()){
            for(Titulo t: listaDeTitulos.listar()){
                if(r.getTitulo().getIsbn().equalsIgnoreCase(t.getIsbn())){
                    r.setTitulo(t);
                    t.getFilaDeReservas().salvar(r);
                }
            }
        }
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

    public void setListaDeUsuarios(UsuarioDAOLista listaDeUsuarios) {
        this.listaDeUsuarios = listaDeUsuarios;
    }

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


}
