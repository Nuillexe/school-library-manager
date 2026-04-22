package model;

public class Livro {
    private String id;
    private boolean disponivel;
    private Titulo titulo;

    public Livro(String id, Titulo titulo) {
        this.id = id;
        this.titulo = titulo;
        this.disponivel = true;
    }

    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean status) { this.disponivel = status; }
    public Titulo getTitulo() { return titulo; }
}

