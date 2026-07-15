package br.edu.ifba.repository.dao;

import br.edu.ifba.models.Emprestimo;
import br.edu.ifba.models.Usuario;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAOLista {
    private List<Emprestimo> listaEmprestimos;

    public EmprestimoDAOLista(int numeroMaximoDeEmprestimos){
        listaEmprestimos=new ArrayList<>();
    }

    public EmprestimoDAOLista(){
        listaEmprestimos=new ArrayList<>();
    }

    public void salvar(Emprestimo e) {
        if (e == null) {
            throw new IllegalArgumentException("Empréstimo não pode ser nulo.");
        }
        listaEmprestimos.add(e);
    }

    public Emprestimo[] listar() {
        Emprestimo[] arrayRetorno = new Emprestimo[listaEmprestimos.size()];
        for (int i =0; i<listaEmprestimos.size(); i++) {
            arrayRetorno[i] = (Emprestimo) listaEmprestimos.get(i);
        }
        return arrayRetorno;
    }

    public Emprestimo[] buscarPorUsuario(Usuario u) {
        int contador = 0;
        for (int i = 0; i < listaEmprestimos.size(); i++) {
            Emprestimo e = (Emprestimo) listaEmprestimos.get(i);
            if (e.getUsuario().equals(u)) {
                contador++;
            }
        }

        Emprestimo[] arrayRetorno = new Emprestimo[contador];
        int indice = 0;

        for (int i = 0; i < listaEmprestimos.size(); i++) {
            Emprestimo e = (Emprestimo) listaEmprestimos.get(i);
            if (e.getUsuario().equals(u)) {
                arrayRetorno[indice++] = e;
            }
        }
        return arrayRetorno;
    }

    public boolean usuarioTemAtraso(Usuario u) {
        for (int  i = 0; i<listaEmprestimos.size(); i++) {
            Emprestimo e = (Emprestimo) listaEmprestimos.get(i);
            if (e.getUsuario().equals(u) && e.isAtrasado()) {
                return true;
            }
        }
        return false;
    }

    public int contarEmprestimosAtivos(Usuario u) {
        int contador = 0;
        for (int i=0; i<listaEmprestimos.size(); i++) {
            Emprestimo e = (Emprestimo) listaEmprestimos.get(i);
            if (e.getUsuario().equals(u) && !e.getLivro().isDisponivel()) {
                contador++;
            }
        }
        return contador;
    }

    public int tamanho(){
        return listaEmprestimos.size();
    }

    public Emprestimo selecionar(int i){
        return listaEmprestimos.get(i);
    }

    public Emprestimo remover(int i){
        return listaEmprestimos.remove(i);
    }

    public Emprestimo apagarPorId(long id) {
        for (int i = 0; i < listaEmprestimos.size(); i++) {
            Emprestimo e = listaEmprestimos.get(i);
            if (e.getId()==id) {
                return listaEmprestimos.remove(i);
            }
        }
        return null;
    }

    public void limpar(){
        listaEmprestimos.clear();
    }
}
