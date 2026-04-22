package controller;

import model.*;
import java.util.List;

public class BibliotecaController {

    public void emprestarLivro(Usuario u, Titulo t) {}

    public void devolverLivro(Usuario u, Livro l) {}

    public void reservarLivro(Usuario u, Titulo t) {}

    public List<Titulo> buscarCatalogo(String nome, String genero) {}

    public List<Emprestimo> verEmprestimos(Usuario u) {}

    public List<Reserva> verReservas(Usuario u) {}
}
