package br.edu.ifba.repository.dao;
import br.edu.ifba.models.Titulo;
import java.util.ArrayList;
import java.util.List;


public class TituloDAOLista {

    private List<Titulo> listaTitulos = new ArrayList<>();

    public void salvar(Titulo t) {
        if (t == null) {
            throw new IllegalArgumentException("Título não pode ser nulo.");
        }
        listaTitulos.add(t);
    }

    public Titulo[] listar() {
        Titulo[] arrayRetorno = new Titulo[listaTitulos.size()];

        for (int i = 0; i < listaTitulos.size(); i++) {
            arrayRetorno[i] = listaTitulos.get(i);
        }
        return arrayRetorno;
    }

    public Titulo buscarPorNome(String nome) {
        for (int i = 0; i < listaTitulos.size(); i++) {
            Titulo t = listaTitulos.get(i);
            if (t.getNome().equalsIgnoreCase(nome)) {
                return t;
            }
        }
        return null;
    }

    public Titulo[] buscarPorGenero(String genero) {
        int contador = 0;

        for (int i = 0; i< listaTitulos.size(); i++) {
            Titulo t = listaTitulos.get(i);
            if (t.getGenero().equalsIgnoreCase(genero)) {
                contador++;
            }
        }
        Titulo[] arrayRetorno = new Titulo[contador];
        int indice = 0;

        for (int i = 0; i < listaTitulos.size(); i++) {
            Titulo t = listaTitulos.get(i);
            if (t.getGenero().equalsIgnoreCase(genero)) {
                arrayRetorno[indice++] = t;
            }
        }
        return arrayRetorno;
    }

    public Titulo buscarPorIsbn(String isbn) {
        if (isbn == null) {
            throw new IllegalArgumentException("ISBN não pode ser nulo.");
        }
        
        for (int i = 0; i < listaTitulos.size(); i++) {
            Titulo titulo = listaTitulos.get(i);

            if (titulo != null && isbn.equalsIgnoreCase(titulo.getIsbn())) {
                return titulo;
            }
        }
        return null;
    }

    public void atualizar(String isbn, Titulo tituloAtualizado) {
        for (int i = 0; i < listaTitulos.size(); i++) {
            Titulo t = listaTitulos.get(i);
            if (t.getIsbn().equals(isbn)) {
                listaTitulos.set(i, tituloAtualizado);
                return;
            }
        }
        throw new IllegalArgumentException("Título com ISBN " + isbn + " não encontrado.");
    }

    public Titulo apagarPorIsbn(String isbn) {
        for (int i = 0; i < listaTitulos.size(); i++) {
            Titulo t = listaTitulos.get(i);
            if (t.getIsbn().equals(isbn)) {
                return listaTitulos.remove(i);
            }
        }
        return null;
    }

    public void ordenar() {
        for (int i = 0; i < listaTitulos.size() - 1; i++) {
            for (int j = 0; j < listaTitulos.size() - i - 1; j++) {
                Titulo titulo1 = listaTitulos.get(j);
                Titulo titulo2 = listaTitulos.get(j + 1);

                if (titulo1.getNome().compareToIgnoreCase(titulo2.getNome()) > 0) {

                    listaTitulos.set(i, titulo2);
                    listaTitulos.set(j + i, titulo2);
                }
            }
        }
    }
}



