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
    public static EmprestimoDAOLista carregarEmprestimos(UsuarioDAOLista usuarioDAO, LivroDAOLista livroDAO) {
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

                if (dados.length >= 8) {
                    int id = Integer.parseInt(dados[0]);
                    String idUsuario = dados[1];
                    String nomeUsuario = dados[2];
                    String nomeLivro = dados[3];
                    String isbn = dados[4];
                    LocalDate dataEmprestimo = LocalDate.parse(dados[5]);
                    LocalDate dataDevolucao = LocalDate.parse(dados[6]);
                    boolean atrasado = Boolean.parseBoolean(dados[7]);

                    // Busca o usuário real carregado na memória
                    Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
                    if (usuario == null) {
                        usuario = new Usuario(idUsuario, nomeUsuario, "", "", TipoUsuario.ALUNO);
                    }

                    // Busca o livro real carregado na memória pelo ISBN
                    Livro livro = null;
                    for (Livro l : livroDAO.listar()) {
                        if (l != null && l.getIsbn().equals(isbn)) {
                            livro = l;
                            break;
                        }
                    }
                    if (livro == null) {
                        livro = new Livro(0, nomeLivro, "", isbn, "", "", LocalDate.now(), true);
                    }

                    Emprestimo emprestimo = new Emprestimo(id, usuario, livro, dataEmprestimo, dataDevolucao, atrasado);
                    dao.salvar(emprestimo);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar empréstimos: " + e.getMessage());
        }
        return dao;
    }

    /**
     * Carrega as reservas vinculando as referências reais de Usuários e Títulos.
     */
    public static ReservaDAOLista carregarReservas(UsuarioDAOLista usuarioDAO, TituloDAOLista tituloDAO) {
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

    public static void salvarLivro(Livro livro) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PASTA_DADOS_LIVROS, true))) {
            bw.write(livro.getId() + SEPARADOR_ESCRITA +
                    livro.getNome() + SEPARADOR_ESCRITA +
                    livro.getAutor() + SEPARADOR_ESCRITA +
                    livro.getIsbn() + SEPARADOR_ESCRITA +
                    livro.getGenero() + SEPARADOR_ESCRITA +
                    livro.getDescricao() + SEPARADOR_ESCRITA +
                    livro.getDataPublicacao() + SEPARADOR_ESCRITA +
                    livro.isDisponivel());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao salvar livro.");
        }
    }

    public static void salvarUsuario(Usuario usuario) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PASTA_DADOS_USUARIOS, true))) {
            bw.write(usuario.getId() + SEPARADOR_ESCRITA +
                    usuario.getNome() + SEPARADOR_ESCRITA +
                    usuario.getEmail() + SEPARADOR_ESCRITA +
                    usuario.getSenha() + SEPARADOR_ESCRITA +
                    usuario.getTipo());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao salvar usuário.");
        }
    }

    public static void salvarEmprestimo(Emprestimo e) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PASTA_DADOS_EMPRESTIMOS, true))) {
            bw.write(e.getId() + SEPARADOR_ESCRITA +
                    e.getUsuario().getId() + SEPARADOR_ESCRITA +
                    e.getUsuario().getNome() + SEPARADOR_ESCRITA +
                    e.getLivro().getNome() + SEPARADOR_ESCRITA +
                    e.getLivro().getIsbn() + SEPARADOR_ESCRITA +
                    e.getDataEmprestimo() + SEPARADOR_ESCRITA +
                    e.getDataDevolucao() + SEPARADOR_ESCRITA +
                    e.isAtrasado());
            bw.newLine();
        } catch (IOException ex) {
            System.out.println("Erro ao salvar empréstimo: " + ex.getMessage());
        }
    }

    public static void salvarReserva(Reserva r) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PASTA_DADOS_RESERVAS, true))) {
            // Nota: Caso sua classe 'Titulo' não possua getIsbn() ou getNome() diretamente,
            // ajuste aqui para buscar através do método correspondente do seu modelo.
            bw.write(r.getId() + SEPARADOR_ESCRITA +
                    r.getUsuario().getId() + SEPARADOR_ESCRITA +
                    r.getUsuario().getNome() + SEPARADOR_ESCRITA +
                    r.getTitulo().getIsbn() + SEPARADOR_ESCRITA +
                    r.getTitulo().getNome() + SEPARADOR_ESCRITA +
                    r.getDataReserva());
            bw.newLine();
        } catch (IOException ex) {
            System.out.println("Erro ao salvar reserva: " + ex.getMessage());
        }
    }

    public static void sobrescreverEmprestimos(EmprestimoDAOLista lista) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PASTA_DADOS_EMPRESTIMOS, false))) {
            for (Emprestimo e : lista.listar()) {
                if (e != null) {
                    bw.write(e.getId() + SEPARADOR_ESCRITA +
                            e.getUsuario().getId() + SEPARADOR_ESCRITA +
                            e.getUsuario().getNome() + SEPARADOR_ESCRITA +
                            e.getLivro().getNome() + SEPARADOR_ESCRITA +
                            e.getLivro().getIsbn() + SEPARADOR_ESCRITA +
                            e.getDataEmprestimo() + SEPARADOR_ESCRITA +
                            e.getDataDevolucao() + SEPARADOR_ESCRITA +
                            e.isAtrasado());
                    bw.newLine();
                }
            }
        } catch (IOException ex) {
            System.out.println("Erro ao sobrescrever empréstimos: " + ex.getMessage());
        }
    }

    public static void sobrescreverReservas(ReservaDAOLista lista) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PASTA_DADOS_RESERVAS, false))) {
            for (Reserva r : lista.listar()) {
                if (r != null) {
                    bw.write(r.getId() + SEPARADOR_ESCRITA +
                            r.getUsuario().getId() + SEPARADOR_ESCRITA +
                            r.getUsuario().getNome() + SEPARADOR_ESCRITA +
                            r.getTitulo().getIsbn() + SEPARADOR_ESCRITA +
                            r.getTitulo().getNome() + SEPARADOR_ESCRITA +
                            r.getDataReserva());
                    bw.newLine();
                }
            }
        } catch (IOException ex) {
            System.out.println("Erro ao sobrescrever reservas: " + ex.getMessage());
        }
    }

    public static void sobrescreverLivros(LivroDAOLista lista) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PASTA_DADOS_LIVROS, false))) {
            for (Livro livro : lista.listar()) {
                if (livro != null) {
                    bw.write(livro.getId() + SEPARADOR_ESCRITA +
                            livro.getNome() + SEPARADOR_ESCRITA +
                            livro.getAutor() + SEPARADOR_ESCRITA +
                            livro.getIsbn() + SEPARADOR_ESCRITA +
                            livro.getGenero() + SEPARADOR_ESCRITA +
                            livro.getDescricao() + SEPARADOR_ESCRITA +
                            livro.getDataPublicacao() + SEPARADOR_ESCRITA +
                            livro.isDisponivel());
                    bw.newLine();
                }
            }
        } catch (IOException ex) {
            System.out.println("Erro ao sobrescrever livros: " + ex.getMessage());
        }
    }
}