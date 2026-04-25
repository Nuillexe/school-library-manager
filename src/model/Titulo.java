package model;
import repository.LivroDaoLista;
import java.time.LocalDate;
import ed.FilaPrioridadeReserva;

public class Titulo {
    private String nome;
    private String autor;
    private String isbn;
    private String genero;
    private String descricao;
    private LocalDate dataPublicacao;
    private int quantidade;
    private int quantidadeDisponivel;
    private int quantidadeReservas;

    private LivroDaoLista listaDeExemplares= new LivroDaoLista();

    public Livro[] allExemplares(){} // Lista todos os exemplares deste do livro com este titulo

    public Livro[] getExemplaresIndisponiveis(){}// Lista todos os exemplares deste do livro com este titulo que foram emprestados

    public Livro[] getExemplaresDisponiveis(){}// Lista todos os exemplares deste do livro que estão disponiveis

    public FilaPrioridadeReserva filaDeReservas; /// não fazer ainda !!!

    public Usuario[] getUsuarioNaListaDeReservas;///não fazer ainda!!!

    public String getNome() {}
    public int getQuantidadeDisponivel() {}
    public void diminuirDisponivel() {}
    public void aumentarDisponivel() {}
}
