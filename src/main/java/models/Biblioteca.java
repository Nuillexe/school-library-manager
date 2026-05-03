package models;

import repository.dao.*;

import java.time.LocalDate;

public class Biblioteca {

    private LivroDAOLista acervo;
    private TituloDAOLista listaDeTitulos;
    private EmprestimoDAOLista listaDeEmprestimos;
    private ReservaDAOLista listaDeReservas; // Para controle de reservas da biblioteca
    private UsuarioDAOLista listaDeUsuarios;

    private static Biblioteca instance;

    private Biblioteca() {

        // Inicializa todos os DAOs diretamente (sem Repository externo)
        listaDeEmprestimos = new EmprestimoDAOLista();
        listaDeReservas = new ReservaDAOLista();
        listaDeUsuarios = new UsuarioDAOLista();
        acervo = new LivroDAOLista();
        listaDeTitulos = new TituloDAOLista();
    }

    // Singleton da Biblioteca
    public static synchronized Biblioteca getInstance() {
        if (instance == null) {
            instance = new Biblioteca();
        }
        return instance;
    }

    // Valida se o ID existe no sistema
    public boolean thisIDIsValid(String id) {

        if (id == null || id.isEmpty()) {
            return false;
        }

        // Professores
        if (id.charAt(0) == 't') {
            for (String idOfTeacher : new String[0]) {
                if (idOfTeacher.equals(id)) {
                    return true;
                }
            }
        }

        // Alunos
        else if (id.charAt(0) == 's') {
            for (String idOfStudent : new String[0]) {
                if (idOfStudent.equals(id)) {
                    return true;
                }
            }
        }

        // Bibliotecários
        else if (id.charAt(0) == 'l') {
            for (String idOfLibrarian : new String[0]) {
                if (idOfLibrarian.equals(id)) {
                    return true;
                }
            }
        }

        return false;
    }

    // ---------------- GETTERS ----------------

    public LivroDAOLista getAcervo() {
        return acervo;
    }

    public EmprestimoDAOLista getListaDeEmprestimos() {
        return listaDeEmprestimos;
    }

    public ReservaDAOLista getListaDeReservas() {
        return listaDeReservas;
    }

    public UsuarioDAOLista getListaDeUsuarios() {
        return listaDeUsuarios;
    }

    // Retorna títulos atualizados sempre que solicitado
    public TituloDAOLista getTitulosAtualizados() {

        this.listaDeTitulos = updateListaDeTitulos(this.acervo);
        return this.listaDeTitulos;
    }

    // ---------------- ATUALIZAÇÃO DE TÍTULOS ----------------

    private TituloDAOLista updateListaDeTitulos(LivroDAOLista acervo) {

        // Garante agrupamento de livros iguais
        acervo.ordenar();

        TituloDAOLista novaListaDeTitulos = new TituloDAOLista();

        int i = 0;

        while (i < acervo.tamanho()) {

            Livro modelo = acervo.selecionar(i);

            if (modelo == null) {
                break;
            }

            LivroDAOLista colecaoExemplares = new LivroDAOLista();

            // Agrupa livros com mesmo ISBN
            while (i < acervo.tamanho()
                    && acervo.selecionar(i) != null
                    && acervo.selecionar(i).getIsbn().equals(modelo.getIsbn())) {

                colecaoExemplares.salvar(acervo.selecionar(i));
                i++;
            }

            // Cria um título a partir dos exemplares agrupados
            novaListaDeTitulos.salvar(new Titulo(colecaoExemplares));
        }

        return novaListaDeTitulos;
    }
}
