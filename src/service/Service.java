package service;

import java.util.List;

public class Service {

    public boolean podeEmprestar(Usuario u) {}

    public void realizarEmprestimo(Usuario u, Titulo t) {}

    public void devolverLivro(Usuario u, Livro l) {}

    public void reservarLivro(Usuario u, Titulo t) {}

    public boolean usuarioTemAtraso(Usuario u) {}

    public List<Titulo> buscarTitulos(String nome, String genero) {}

    public List<Emprestimo> listarEmprestimosUsuario(Usuario u) {}

    public List<Reserva> listarReservasUsuario(Usuario u) {}
}