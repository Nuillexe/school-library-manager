package br.edu.ifba.repository;

// Imports das suas classes de modelo
import br.edu.ifba.models.Livro;
import br.edu.ifba.models.Usuario;
import br.edu.ifba.models.Emprestimo;
import br.edu.ifba.models.Reserva;
import br.edu.ifba.models.Titulo;

// Import explícito do Enum do seu projeto
import br.edu.ifba.enums.TipoUsuario;

// Imports dos seus DAOs customizados
import br.edu.ifba.repository.dao.LivroDAOLista;
import br.edu.ifba.repository.dao.UsuarioDAOLista;
import br.edu.ifba.repository.dao.EmprestimoDAOLista;
import br.edu.ifba.repository.dao.ReservaDAOLista;
import br.edu.ifba.repository.dao.ReservaDAOFilaDePrioridade;

// Imports nativos do Java para manipulação de arquivos (I/O) e datas
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PersistenceManager {

    // Constantes com as pastas e caminhos dos arquivos .txt fornecidos no esqueleto
    private static final String PASTA_DADOS_LIVROS = "resources/data/livros.txt";
    private static final String PASTA_DADOS_RESERVAS = "resources/data/reservas.txt";
    private static final String PASTA_DADOS_EMPRESTIMOS = "resources/data/emprestimos.txt";
    private static final String PASTA_DADOS_USUARIOS = "resources/data/usuarios.txt";
    private static final String PASTA_DADOS_IDS = "resources/data/ids.txt";

    private static final String SEPARADOR_LEITURA = "\\|";
    private static final String SEPARADOR_ESCRITA = "|";

    // =========================================================================
    // MÉTODOS DE LEITURA (CARREGAMENTO DOS ARQUIVOS TXT PARA A MEMÓRIA)
    // =========================================================================

    /**
     * Lê o arquivo de livros e reconstrói o LivroDAOLista.
     */
    public static LivroDAOLista carregarLivros() {
        LivroDAOLista dao = new LivroDAOLista();
        File arquivo = new File(PASTA_DADOS_LIVROS);
        if (!arquivo.exists()) return dao;

        // O bloco try-with-resources garante o fechamento automático do arquivo
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.isBlank()) continue;
                String[] dados = linha.split(SEPARADOR_LEITURA);
                if (dados.length >= 8) {
                    // Recupera os dados guardados em formato texto (CSV separado por ';')
                    String nome = dados[1];
                    String autor = dados[2];
                    String isbn = dados[3];
                    String genero = dados[4];
                    String descricao = dados[5];
                    LocalDate dataPub = LocalDate.parse(dados[6]);
                    boolean disponivel = Boolean.parseBoolean(dados[7]);

                    // Recria o objeto livro usando o construtor padrão do grupo
                    Livro livro = new Livro(nome, autor, isbn, genero, descricao, dataPub);
                    livro.setDisponivel(disponivel);

                    dao.salvar(livro);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar o arquivo de livros: " + e.getMessage());
        }
        return dao;
    }

    /**
     * Lê o arquivo de usuários cadastrados e monta o UsuarioDAOLista.
     */
    public static UsuarioDAOLista carregarUsuarios() {
        UsuarioDAOLista dao = new UsuarioDAOLista();
        File arquivo = new File(PASTA_DADOS_USUARIOS);
        if (!arquivo.exists()) return dao;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.isBlank()) continue;
                String[] dados = linha.split(SEPARADOR_LEITURA);
                if (dados.length >= 5) {
                    String id = dados[0];
                    String nome = dados[1];
                    String email = dados[2];
                    String senha = dados[3];
                    // Converte o texto estrito de volta para a constante correspondente do Enum TipoUsuario
                    TipoUsuario tipo = TipoUsuario.valueOf(dados[4]);

                    Usuario u = new Usuario(id, nome, email, senha, tipo);
                    dao.salvar(u);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar o arquivo de usuários: " + e.getMessage());
        }
        return dao;
    }

    /**
     * Carrega o histórico de empréstimos da biblioteca a partir do arquivo txt.
     */
    public static EmprestimoDAOLista carregarEmprestimos() {
        EmprestimoDAOLista dao = new EmprestimoDAOLista();
        File arquivo = new File(PASTA_DADOS_EMPRESTIMOS);
        if (!arquivo.exists()) return dao;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.isBlank()) continue;
                String[] dados = linha.split(SEPARADOR_LEITURA);
                if (dados.length >= 9) {
                    String userIdentificador = dados[1];
                    String userNome = dados[2];
                    String livroNome = dados[4];
                    String livroIsbn = dados[5];
                    LocalDate dataEmp = LocalDate.parse(dados[6]);
                    LocalDate dataDev = LocalDate.parse(dados[7]);
                    boolean atrasado = Boolean.parseBoolean(dados[8]);

                    // Instancia Stubs temporários para manter o vínculo visual exigido nas tabelas da interface gráfica
                    Usuario userStub = new Usuario(userIdentificador, userNome, "", "", TipoUsuario.ALUNO);
                    Livro livroStub = new Livro(livroNome, "", livroIsbn, "", "", LocalDate.now());

                    Emprestimo emp = new Emprestimo(userStub, livroStub);
                    emp.setDataEmprestimo(dataEmp);
                    emp.setDataDevolucao(dataDev);
                    emp.setAtrasado(atrasado);

                    dao.salvar(emp);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar o arquivo de empréstimos: " + e.getMessage());
        }
        return dao;
    }

    /**
     * Carrega a listagem global de reservas do arquivo de persistência.
     */
    public static ReservaDAOLista carregarReservas() {
        ReservaDAOLista dao = new ReservaDAOLista();
        File arquivo = new File(PASTA_DADOS_RESERVAS);
        if (!arquivo.exists()) return dao;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.isBlank()) continue;
                String[] dados = linha.split(SEPARADOR_LEITURA);
                if (dados.length >= 6) {
                    String userIdentificador = dados[1];
                    String userNome = dados[2];
                    String tituloIsbn = dados[3];
                    String tituloNome = dados[4];
                    LocalDateTime dataRes = LocalDateTime.parse(dados[5]);

                    // Monta a hierarquia mínima necessária exigida pelo modelo Titulo/Reserva
                    Usuario userStub = new Usuario(userIdentificador, userNome, "", "", TipoUsuario.ALUNO);
                    Livro livroModelo = new Livro(tituloNome, "", tituloIsbn, "", "", LocalDate.now());
                    LivroDAOLista exLista = new LivroDAOLista();
                    exLista.salvar(livroModelo);
                    Titulo tituloStub = new Titulo(exLista, new EmprestimoDAOLista(), new ReservaDAOFilaDePrioridade());

                    Reserva res = new Reserva(userStub, tituloStub);
                    res.setDataReserva(dataRes);

                    dao.salvar(res);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar o arquivo de reservas: " + e.getMessage());
        }
        return dao;
    }

    /**
     * Lê as chaves brutas de IDs autorizados no sistema da biblioteca.
     */
    public static ArrayList<String> carregarIds() {
        ArrayList<String> ids = new ArrayList<>();
        File arquivo = new File(PASTA_DADOS_IDS);
        if (!arquivo.exists()) return ids;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (!linha.isBlank()) {
                    ids.add(linha.trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar os IDs autorizados: " + e.getMessage());
        }
        return ids;
    }

    // =========================================================================
    // MÉTODOS DE ESCRITA (SALVAMENTO UNITÁRIO / APPEND / SOBREESCRITA)
    // =========================================================================

    /**
     * Salva ou atualiza a listagem completa de exemplares físicos do acervo.
     */
    public static void salvarLivros(LivroDAOLista livros) {
        if (livros == null) return;
        File arquivo = new File(PASTA_DADOS_LIVROS);
        criarPastasSeNaoExistirem(arquivo);

        // 'false' indica que o arquivo será totalmente reescrito com o acervo atualizado
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, false))) {
            for (Livro l : livros.listar()) {
                if (l == null) continue;
                bw.write(
                        l.getId() + SEPARADOR_ESCRITA +
                                l.getNome() + SEPARADOR_ESCRITA +
                                l.getAutor() + SEPARADOR_ESCRITA +
                                l.getIsbn() + SEPARADOR_ESCRITA +
                                l.getGenero() + SEPARADOR_ESCRITA +
                                l.getDescricao() + SEPARADOR_ESCRITA +
                                l.getDataPublicacao() + SEPARADOR_ESCRITA +
                                l.isDisponivel()
                );
                bw.newLine(); // Pula para a próxima linha do arquivo .txt
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar catálogo de livros: " + e.getMessage());
        }
    }

    /**
     * Anexa (Append) um novo usuário cadastrado diretamente ao fim do arquivo de texto.
     */
    public static void salvarUsuario(Usuario u) {
        if (u == null) return;
        File arquivo = new File(PASTA_DADOS_USUARIOS);
        criarPastasSeNaoExistirem(arquivo);

        // 'true' ativa o modo append (grava ao final do arquivo sem apagar o que já existe)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, true))) {
            bw.write(
                    u.getId() + SEPARADOR_ESCRITA +
                            u.getNome() + SEPARADOR_ESCRITA +
                            u.getEmail() + SEPARADOR_ESCRITA +
                            u.getSenha() + SEPARADOR_ESCRITA +
                            u.getTipo()
            );
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao anexar novo usuário: " + e.getMessage());
        }
    }

    /**
     * Anexa (Append) um novo registro de empréstimo feito ao fim do arquivo txt.
     */
    public static void salvarEmprestimo(Emprestimo e) {
        if (e == null) return;
        File arquivo = new File(PASTA_DADOS_EMPRESTIMOS);
        criarPastasSeNaoExistirem(arquivo);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, true))) {
            bw.write(
                    e.getId() + SEPARADOR_ESCRITA +
                            e.getUsuario().getId() + SEPARADOR_ESCRITA +
                            e.getUsuario().getNome() + SEPARADOR_ESCRITA +
                            e.getLivro().getId() + SEPARADOR_ESCRITA +
                            e.getLivro().getNome() + SEPARADOR_ESCRITA +
                            e.getLivro().getIsbn() + SEPARADOR_ESCRITA +
                            e.getDataEmprestimo() + SEPARADOR_ESCRITA +
                            e.getDataDevolucao() + SEPARADOR_ESCRITA +
                            e.isAtrasado()
            );
            bw.newLine();
        } catch (IOException ex) { // <-- Alterado aqui de 'e' para 'ex'
            System.out.println("Erro ao anexar novo empréstimo: " + ex.getMessage()); // <-- E aqui também
        }
    }

    /**
     * Anexa (Append) uma nova reserva gerada ao fim do arquivo txt.
     */
    public static void salvarReserva(Reserva r) {
        if (r == null) return;
        File arquivo = new File(PASTA_DADOS_RESERVAS);
        criarPastasSeNaoExistirem(arquivo);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, true))) {
            bw.write(
                    r.getId() + SEPARADOR_ESCRITA +
                            r.getUsuario().getId() + SEPARADOR_ESCRITA +
                            r.getUsuario().getNome() + SEPARADOR_ESCRITA +
                            r.getTitulo().getIsbn() + SEPARADOR_ESCRITA +
                            r.getTitulo().getNome() + SEPARADOR_ESCRITA +
                            r.getDataReserva()
            );
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao anexar nova reserva: " + e.getMessage());
        }
    }

    /**
     * Sobrescreve a lista completa de empréstimos (usado ao devolver livros ou recalcular atrasos).
     */
    public static void sobrescreverEmprestimos(EmprestimoDAOLista listaDeEmprestimos) {
        if (listaDeEmprestimos == null) return;
        File arquivo = new File(PASTA_DADOS_EMPRESTIMOS);
        criarPastasSeNaoExistirem(arquivo);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, false))) {
            for (Emprestimo e : listaDeEmprestimos.listar()) {
                if (e == null) continue;
                bw.write(
                        e.getId() + SEPARADOR_ESCRITA +
                                e.getUsuario().getId() + SEPARADOR_ESCRITA +
                                e.getUsuario().getNome() + SEPARADOR_ESCRITA +
                                e.getLivro().getId() + SEPARADOR_ESCRITA +
                                e.getLivro().getNome() + SEPARADOR_ESCRITA +
                                e.getLivro().getIsbn() + SEPARADOR_ESCRITA +
                                e.getDataEmprestimo() + SEPARADOR_ESCRITA +
                                e.getDataDevolucao() + SEPARADOR_ESCRITA +
                                e.isAtrasado()
                );
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao sincronizar histórico de empréstimos: " + e.getMessage());
        }
    }

    /**
     * Sobrescreve a lista de reservas (usado quando um usuário desiste ou assume uma reserva).
     */
    public static void sobrescreverReservas(ReservaDAOLista listaDeReservas) {
        if (listaDeReservas == null) return;
        File arquivo = new File(PASTA_DADOS_RESERVAS);
        criarPastasSeNaoExistirem(arquivo);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, false))) {
            for (Reserva r : listaDeReservas.listar()) {
                if (r == null) continue;
                bw.write(
                        r.getId() + SEPARADOR_ESCRITA +
                                r.getUsuario().getId() + SEPARADOR_ESCRITA +
                                r.getUsuario().getNome() + SEPARADOR_ESCRITA +
                                r.getTitulo().getIsbn() + SEPARADOR_ESCRITA +
                                r.getTitulo().getNome() + SEPARADOR_ESCRITA +
                                r.getDataReserva()
                );
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao sincronizar fila de reservas: " + e.getMessage());
        }
    }

    // =========================================================================
    // UTILITÁRIO INTERNO (EVITA REPETIÇÃO DE CÓDIGO)
    // =========================================================================

    /**
     * Garante de forma nativa e segura que as pastas (resources/data/) existam no computador.
     */
    private static void criarPastasSeNaoExistirem(File arquivo) {
        File pastaPai = arquivo.getParentFile();
        if (pastaPai != null && !pastaPai.exists()) {
            pastaPai.mkdirs(); // Cria todas as pastas do caminho que não existirem
        }
    }
}