//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import model.*;
import repository.*;

public class Main {
    public static void main(String[] args) {

        Biblioteca biblioteca = new Biblioteca();

        biblioteca.livroDao = new LivroDaoLista();
        biblioteca.tituloDao = new TituloDaoLista();
        biblioteca.emprestimoDao = new EmprestimoDaoLista();
        biblioteca.reservaDao = new ReservaDaoLista();

        System.out.println("Sistema iniciado!");
    }
}