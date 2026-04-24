package model;

import java.util.List;

public class Usuario {
    private String id;
    private String nome;
    private String email;
    private String senha;
    private TipoUsuario categoria;
    private int limiteLivros;
    private List<Emprestimo> emprestimos;

    public String getId() {}
    public String getNome() {}
    public TipoUsuario getCategoria() {}
    public List<Emprestimo> getEmprestimos() {}
}
