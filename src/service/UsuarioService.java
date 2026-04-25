package service;


import model.*;
import repository.*;

import java.sql.ClientInfoStatus;

public class UsuarioService {
    private Biblioteca biblioteca;
    private LivroDaoLista acervo;//lista de livros da biblioteca
    private TituloDaoLista listaDeTitulos;
    private Usuario user;

    public UsuarioService(Biblioteca biblioteca, Usuario userLogado) {
        this.biblioteca = biblioteca;
        this.acervo = biblioteca.acervo;
        this.listaDeTitulos = biblioteca.listaDeTitulos;
        this.user = userLogado;
    }

    //pegarEmprestimo
    //devoluçaoDoEmprestimo
    //Mostrar Catalogo de livros: listaDeTitulos


}
