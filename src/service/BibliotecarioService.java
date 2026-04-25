package service;

import model.*;
import repository.*;

public class BibliotecarioService {
    private Biblioteca biblioteca;
    private LivroDaoLista listaDeLivros;
    private TituloDaoLista listaDeTitulos;
    private Usuario user;
    private UsuarioDAOLista listaDeUsuarios;

    public BibliotecarioService(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        this.listaDeLivros = listaDeLivros;
        this.listaDeTitulos = listaDeTitulos;
        this.listaDeUsuarios = listaDeUsuarios;
        this.user = user;
    }

    //Funçoes do Biblotecario. Por exemplo, get
}
