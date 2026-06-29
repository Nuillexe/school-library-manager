package br.edu.ifba.repository.dao;

import br.edu.ifba.models.Livro;
import java.util.ArrayList;
import java.util.List;

public class LivroDAOLista {

    private List<Livro> listaLivros = new ArrayList<>();

    public void salvar(Livro l) {
        if (l == null) {
            throw new IllegalArgumentException("Livro não pode ser nulo.");
        }
        listaLivros.add(l);
    }

    public Livro buscarPorId(Long id) {
        for (int i = 0; i < listaLivros.size(); i++) {
            Livro l = listaLivros.get(i);
            if (l != null && l.getId().equals(id)) {
                return l;
            }
        }
        return null;
    }

    public Livro[] listar() {
        Livro[] arrayRetorno = new Livro[listaLivros.size()];
        for (int i = 0; i < listaLivros.size(); i++) {
            arrayRetorno[i] = listaLivros.get(i);
        }
        return arrayRetorno;
    }

    public void atualizar(Long id, Livro livroAtualizado) {
        for (int i = 0; i < listaLivros.size(); i++) {
            Livro l = listaLivros.get(i);
            if (l != null && l.getId().equals(id)) {
                listaLivros.set(i, livroAtualizado);
                return;
            }
        }
        throw new IllegalArgumentException("Livro com ID " + id + " não encontrado.");
    }

    public Livro apagar(Long id) {
        for (int i = 0; i < listaLivros.size(); i++) {
            Livro l = listaLivros.get(i);
            if (l != null && l.getId().equals(id)) {
                return listaLivros.remove(i);
            }
        }
        return null;
    }

    public int contarExemplares(String nome) {
        int contador = 0;
        for (int i = 0; i < listaLivros.size(); i++) {
            Livro l = listaLivros.get(i);
            if (l != null && l.getNome().equalsIgnoreCase(nome)) {
                contador++;
            }
        }
        return contador;
    }

    public int contarDisponiveis(String nome) {
        int contador = 0;
        for (int i = 0; i < listaLivros.size(); i++) {
            Livro l = listaLivros.get(i);
            if (l != null && l.getNome().equalsIgnoreCase(nome) && l.isDisponivel()) {
                contador++;
            }
        }
        return contador;
    }

    /// get livros disponiveis
    public Livro[] getDisponiveis(){
        int contador = 0;
        Livro l;

        for (int i = 0; i < listaLivros.size(); i++) {
            l = listaLivros.get(i);
            if (l != null && l.isDisponivel())
                contador++;
        }

        Livro[] livrosDisponiveis = new Livro[contador];
        int y = 0;

        for(int i = 0; i < listaLivros.size(); i++){
            l = listaLivros.get(i);
            if (l != null && l.isDisponivel()){
                livrosDisponiveis[y++] = l;
            }
        }

        return livrosDisponiveis;
    }

    /// get livros Indisponiveis
    public Livro[] getIndisponiveis(){
        int contador = 0;
        Livro l;

        for (int i = 0; i < listaLivros.size(); i++) {
            l = listaLivros.get(i);
            if (l != null && !l.isDisponivel())
                contador++;
        }

        Livro[] livrosIndisponiveis = new Livro[contador];
        int y = 0;

        for(int i = 0; i < listaLivros.size(); i++){
            l = listaLivros.get(i);
            if (l != null && !l.isDisponivel()){
                livrosIndisponiveis[y++] = l;
            }
        }

        return livrosIndisponiveis;
    }

    public Livro selecionar(int i){
        return listaLivros.get(i);
    }

    public int quantidade(){
        return listaLivros.size();
    }

    public int tamanho(){
        return quantidade();
    }

    public void ordenar() {
        listaLivros.sort(
                (l1, l2) -> l1.getIsbn().compareToIgnoreCase(l2.getIsbn())
        );
    }
}
