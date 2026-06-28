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
import br.edu.ifba.repository.dao.TituloDAOLista;
import br.edu.ifba.repository.dao.UsuarioDAOLista;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PersistenceManager {

    private static final String PASTA_DADOS_LIVROS = "resources/data/livros.txt";
    private static final String PASTA_DADOS_RESERVAS = "resources/data/reservas.txt";
    private static final String PASTA_DADOS_EMPRESTIMOS = "resources/data/emprestimos.txt";
    private static final String PASTA_DADOS_USUARIOS = "resources/data/usuarios.txt";
    private static final String PASTA_DADOS_IDS = "resources/data/ids.txt";

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

                if (dados.length >= 6) {
                    int id = Integer.parseInt(dados[0]);
                    String idUsuario = dados[1];
                    String nomeUsuario = dados[2];
                    String isbn = dados[3];
                    String nomeTitulo = dados[4];
                    LocalDateTime dataReserva = LocalDateTime.parse(dados[5]);

                    // Busca o usuário real
                    Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
                    if (usuario == null) {
                        usuario = new Usuario(idUsuario, nomeUsuario, "", "", TipoUsuario.ALUNO);
                    }

                    // Busca o título real
                    Titulo titulo = tituloDAO.buscarPorNome(nomeTitulo);
                    if (titulo == null) {
                        Livro livro = new Livro(0, nomeTitulo, "", isbn, "", "", LocalDate.now(), true);
                        LivroDAOLista lista = new LivroDAOLista();
                        lista.salvar(livro);
                        titulo = new Titulo(lista);
                    }

                    Reserva reserva = new Reserva(id, usuario, titulo, dataReserva);
                    dao.salvar(reserva);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar reservas: " + e.getMessage());
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


}