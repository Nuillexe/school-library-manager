package model;

import java.time.LocalDate;

public class Emprestimo {
    private String id;
    private Usuario usuario;
    private Livro livro;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucao;
    private boolean atrasado;

    public boolean isAtrasado() {}
    public Usuario getUsuario() {}
    public Livro getLivro() {}
}
