package br.edu.ifba.models;

import br.edu.ifba.enums.TipoUsuario;
import br.edu.ifba.repository.dao.EmprestimoDAOLista;

public class Usuario {
    private String id;
    private String nome;
    private String email;
    private String senha;
    public TipoUsuario tipo;
    private int limiteLivros;
    private EmprestimoDAOLista listaEmprestimos;

    public Usuario(String id, String nome, String email, String senha, TipoUsuario tipo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipo = tipo;
        this.senha = senha;

        if(tipo == TipoUsuario.ALUNO){
            limiteLivros = 3;
        } else if(tipo == TipoUsuario.PROFESSOR){
            limiteLivros = 4;
        } else {
            limiteLivros = 5; // evita valor 0 para bibliotecário
        }

        this.listaEmprestimos = new EmprestimoDAOLista();
    }


    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public TipoUsuario getTipo() { return tipo; }
    public int getLimiteLivros() { return limiteLivros; }
    public EmprestimoDAOLista getListaEmprestimos() { return listaEmprestimos; }


    // Setters
    public void setId(String id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setCategoria(TipoUsuario categoria) { this.tipo = categoria; }
    public void setLimiteLivros(int limiteLivros) { this.limiteLivros = limiteLivros; }

    public void mostrarDados() {
        System.out.println("ID: " + id + " | Nome: " + nome + " | Categoria: " + tipo);
    }

    public void adicionarEmprestimo(Emprestimo emprestimo) {
        /// Adiciona um emprestimo na lista

        /// Verifica se o emprestimo é nulo
        if (emprestimo == null) {
            return;
        }

        listaEmprestimos.salvar(emprestimo);
    }

    /// Remove um emprestimo pelo id
    public Emprestimo removerEmprestimo(Emprestimo e){

        /// Verifica se o objeto é válido
        if(e == null){
            return null;
        }

        /// Percorre a lista usando índice
        for(int i = 0; i < listaEmprestimos.tamanho(); i++){

            Emprestimo emp = listaEmprestimos.selecionar(i);

            /// Compara os IDs (tipo primitivo usa ==)
            if(emp != null && emp.getId() == e.getId()){
                return listaEmprestimos.remover(i);
            }
        }

        return null;
    }

    public boolean temEmprestimoAtrasado(){
        /// Retorna true se algum emprestimo estiver atrasado

        for(int i = 0; i < listaEmprestimos.tamanho(); i++){

            Emprestimo emp = listaEmprestimos.selecionar(i);

            if(emp != null && emp.isAtrasado()){
                return true;
            }
        }

        return false;
    }

    /// Compara dois usuarios pelo id
    @Override
    public boolean equals(Object obj){

        /// Verifica se é o mesmo objeto
        if(this == obj){
            return true;
        }

        /// Verifica se é nulo ou tipo diferente
        if(obj == null || getClass() != obj.getClass()){
            return false;
        }

        Usuario outro = (Usuario) obj;

        /// Verifica se os ids são válidos
        if(this.id == null || outro.id == null){
            return false;
        }

        return this.id.equals(outro.id);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "email='" + email + '\'' +
                "senha: "+senha+'}';
    }
}
