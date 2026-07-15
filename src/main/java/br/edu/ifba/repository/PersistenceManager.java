package br.edu.ifba.repository;

import br.edu.ifba.enums.TipoUsuario;
import br.edu.ifba.models.Emprestimo;
import br.edu.ifba.models.Livro;
import br.edu.ifba.models.Reserva;
import br.edu.ifba.models.Titulo;
import br.edu.ifba.models.Usuario;
import br.edu.ifba.repository.dao.EmprestimoDAOLista;
import br.edu.ifba.repository.dao.LivroDAOLista;
import br.edu.ifba.repository.dao.ReservaDAOLista;
import br.edu.ifba.repository.dao.UsuarioDAOLista;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PersistenceManager {

    private static final String PASTA_DADOS_LIVROS = "data/livros.txt";
    private static final String PASTA_DADOS_RESERVAS = "data/reservas.txt";
    private static final String PASTA_DADOS_EMPRESTIMOS = "data/emprestimos.txt";
    private static final String PASTA_DADOS_USUARIOS = "data/usuarios.txt";
    private static final String PASTA_DADOS_IDS = "data/ids.txt";

    private static final String SEPARADOR_LEITURA = "\\|";
    private static final String SEPARADOR_ESCRITA = "|";

    // ============================================================
    // LEITURA DOS ARQUIVOS
    // ============================================================

    public static LivroDAOLista carregarLivros() {
        LivroDAOLista dao = new LivroDAOLista();
        File arquivo = new File(PASTA_DADOS_LIVROS);

        if (!arquivo.exists()) {
            return dao;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.isBlank()) {
                    continue;
                }

                String[] dados = linha.split(SEPARADOR_LEITURA);

                if (dados.length >= 8) {
                    int id = Integer.parseInt(dados[0]);
                    String nome = dados[1];
                    String autor = dados[2];
                    String isbn = dados[3];
                    String genero = dados[4];
                    String descricao = dados[5];
                    LocalDate dataPublicacao = LocalDate.parse(dados[6]);
                    boolean disponivel = Boolean.parseBoolean(dados[7]);

                    Livro livro = new Livro(id, nome, autor, isbn, genero, descricao, dataPublicacao, disponivel);
                    dao.salvar(livro);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar livros: " + e.getMessage());
        }
        return dao;
    }

    public static UsuarioDAOLista carregarUsuarios() {
        UsuarioDAOLista dao = new UsuarioDAOLista();
        File arquivo = new File(PASTA_DADOS_USUARIOS);

        if (!arquivo.exists()) {
            return dao;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.isBlank()) {
                    continue;
                }

                String[] dados = linha.split(SEPARADOR_LEITURA);

                if (dados.length >= 5) {
                    String id = dados[0];
                    String nome = dados[1];
                    String email = dados[2];
                    String senha = dados[3];
                    TipoUsuario tipo = TipoUsuario.valueOf(dados[4]);

                    Usuario usuario = new Usuario(id, nome, email, senha, tipo);
                    dao.salvar(usuario);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar usuários: " + e.getMessage());
        }
        return dao;
    }

    /**
     * Carrega os empréstimos vinculando as referências reais de Usuários e Livros na memória.
     */
    public static EmprestimoDAOLista carregarEmprestimos() {
        EmprestimoDAOLista dao = new EmprestimoDAOLista();
        File arquivo = new File(PASTA_DADOS_EMPRESTIMOS);

        if (!arquivo.exists()) {
            return dao;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {

            String linha;

            while ((linha = br.readLine()) != null) {

                if (linha.isBlank()) {
                    continue;
                }

                String[] dados = linha.split(SEPARADOR_LEITURA);

                if (dados.length >= 6) {

                    long id = Long.parseLong(dados[0]);

                    LocalDate dataEmprestimo = LocalDate.parse(dados[1]);

                    LocalDate dataDevolucao = LocalDate.parse(dados[2]);

                    boolean atrasado = Boolean.parseBoolean(dados[3]);

                    String idUsuario = dados[4];

                    long idLivro = Long.parseLong(dados[5]);

                    // Objetos temporários
                    Usuario usuarioFake = new Usuario(idUsuario);

                    Livro livroFake = new Livro(idLivro);

                    Emprestimo emprestimo = new Emprestimo(id,
                                    usuarioFake,
                                    livroFake,
                                    dataEmprestimo,
                                    dataDevolucao,
                                    atrasado
                            );

                    dao.salvar(emprestimo);
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao carregar empréstimos: "
                    + e.getMessage());
        }

        return dao;
    }
    /**
     * Carrega as reservas vinculando as referências reais de Usuários e Títulos.
     */
    public static ReservaDAOLista carregarReservas() {

        ReservaDAOLista dao = new ReservaDAOLista();
        File arquivo = new File(PASTA_DADOS_RESERVAS);

        if (!arquivo.exists()) {
            return dao;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {

            String linha;

            while ((linha = br.readLine()) != null) {

                if (linha.isBlank()) {
                    continue;
                }

                String[] dados = linha.split(SEPARADOR_LEITURA);

                if (dados.length >= 4) {

                    long id =
                            Long.parseLong(dados[0]);

                    String idUsuario =
                            dados[1];

                    String isbnTitulo =
                            dados[2];

                    LocalDateTime dataReserva =
                            LocalDateTime.parse(dados[3]);

                    // Objetos temporários
                    Usuario usuarioFake =
                            new Usuario(idUsuario);

                    Titulo tituloFake =
                            new Titulo(isbnTitulo);

                    Reserva reserva =
                            new Reserva(
                                    id,
                                    usuarioFake,
                                    tituloFake,
                                    dataReserva
                            );

                    dao.salvar(reserva);
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao carregar reservas: "
                    + e.getMessage());
        }

        return dao;
    }

    public static ArrayList<String> carregarIds() {
        ArrayList<String> ids = new ArrayList<>();
        File arquivo = new File(PASTA_DADOS_IDS);

        if (!arquivo.exists()) {
            return ids;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (!linha.isBlank()) {
                    ids.add(linha.trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar IDs: " + e.getMessage());
        }
        return ids;
    }

    // ============================================================
    // ESCRITA DOS ARQUIVOS
    // ============================================================
    public static void salvarLivro(Livro livro) {
        try {
            FileWriter fw = new FileWriter(PASTA_DADOS_LIVROS, true);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(
                    livro.getId() + "|" +
                            livro.getNome() + "|" +
                            livro.getAutor() + "|" +
                            livro.getIsbn() + "|" +
                            livro.getGenero() + "|" +
                            livro.getDescricao() + "|" +
                            livro.getDataPublicacao() + "|" +
                            livro.isDisponivel()
            );

            bw.newLine();
            bw.close();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar livro.", e);
        }
    }

    public static void salvarUsuario(Usuario u) {
        try {
            FileWriter fw = new FileWriter(PASTA_DADOS_USUARIOS, true);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(
                    u.getId() + "|" +
                            u.getNome() + "|" +
                            u.getEmail() + "|" +
                            u.getSenha() + "|" +
                            u.getTipo()
            );

            bw.newLine();
            bw.close();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar usuário.", e);
        }
    }

    public static void salvarEmprestimo(Emprestimo e) {
        try {
            FileWriter fw = new FileWriter(PASTA_DADOS_EMPRESTIMOS, true);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(
                    e.getId() + "|" +
                            e.getDataEmprestimo() + "|" +
                            e.getDataDevolucao() + "|" +
                            e.isAtrasado() + "|" +
                            e.getUsuario().getId() + "|" +
                            e.getLivro().getId()
            );

            bw.newLine();
            bw.close();

        } catch (IOException e1) {
            throw new RuntimeException("Erro ao salvar empréstimo.", e1);
        }
    }

    public static void salvarReserva(Reserva r) {
        try {
            FileWriter fw = new FileWriter(PASTA_DADOS_RESERVAS, true);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(
                    r.getId() + "|" +
                            r.getUsuario().getId() + "|" +
                            r.getTitulo().getIsbn() + "|" +
                            r.getDataReserva()
            );

            bw.newLine();
            bw.close();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar reserva.", e);
        }
    }

    public static void sobrescreverEmprestimos(EmprestimoDAOLista listaDeEmprestimos) {

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(PASTA_DADOS_EMPRESTIMOS))) {

            for (int i = 0; i < listaDeEmprestimos.tamanho(); i++) {

                Emprestimo e = listaDeEmprestimos.selecionar(i);

                bw.write(
                        e.getId() + "|" +
                                e.getDataEmprestimo() + "|" +
                                e.getDataDevolucao() + "|" +
                                e.isAtrasado() + "|" +
                                e.getUsuario().getId() + "|" +
                                e.getLivro().getId()
                );

                bw.newLine();
            }

            bw.close();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao sobrescrever empréstimos.", e);
        }
    }

    public static void sobrescreverReservas(ReservaDAOLista listaDeReservas) {

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(PASTA_DADOS_RESERVAS))) {

            for (Reserva r : listaDeReservas.listar()) {

                bw.write(
                        r.getId() + "|" +
                                r.getUsuario().getId() + "|" +
                                r.getTitulo().getIsbn() + "|" +
                                r.getDataReserva()
                );

                bw.newLine();
            }

            bw.close();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao sobrescrever reservas.", e);
        }
    }

    public static void sobrescreverUsuarios(UsuarioDAOLista listaDeUsuarios) {

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(PASTA_DADOS_USUARIOS))) {
            Usuario[] usuarios = listaDeUsuarios.listar();

            for (Usuario u : usuarios) {

                bw.write(
                        u.getId() + "|" +
                                u.getNome() + "|" +
                                u.getEmail() + "|" +
                                u.getSenha() + "|" +
                                u.getTipo()
                );

                bw.newLine();
            }

            bw.close();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao sobrescrever usuários.", e);
        }
    }

    public static void sobrescreverLivros(LivroDAOLista listaDeLivros) {

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(PASTA_DADOS_LIVROS))) {

            for (int i = 0; i < listaDeLivros.tamanho(); i++) {

                Livro livro = listaDeLivros.selecionar(i);

                bw.write(
                        livro.getId() + "|" +
                                livro.getNome() + "|" +
                                livro.getAutor() + "|" +
                                livro.getIsbn() + "|" +
                                livro.getGenero() + "|" +
                                livro.getDescricao() + "|" +
                                livro.getDataPublicacao() + "|" +
                                livro.isDisponivel()
                );

                bw.newLine();
            }

            bw.close();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao sobrescrever livros.", e);
        }
    }

    public static void limparReservas() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PASTA_DADOS_RESERVAS))) {
            // abre o arquivo sem append e não escreve nada
        } catch (IOException e) {
            throw new RuntimeException("Erro ao limpar arquivo de reservas.", e);
        }
    }

    public static void limparEmprestimos() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PASTA_DADOS_EMPRESTIMOS))) {
            // abre o arquivo sem append e não escreve nada
        } catch (IOException e) {
            throw new RuntimeException("Erro ao limpar arquivo de reservas.", e);
        }
    }

    public static void apagarTodosOsUsuariosCriados(){
        apagarArquivoAPartirDaLinha(PASTA_DADOS_USUARIOS, 14);
    }

    public static void apagarArquivoAPartirDaLinha(String caminho, int linhaInicial) {
        try {
            Path path = Paths.get(caminho);

            List<String> linhas = Files.readAllLines(path);

            if (linhaInicial < 0 || linhaInicial >= linhas.size()) {
                return;
            }

            linhas.subList(linhaInicial, linhas.size()).clear();

            Files.write(path, linhas);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao apagar linhas do arquivo.", e);
        }
    }
}

