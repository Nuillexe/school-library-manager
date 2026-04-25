package service;

import model.Biblioteca;
import model.Usuario;

import java.util.Scanner;

// Login e cadastro
public class AuthService {

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Realiza o login do usuário.
     * Verifica se o email e a senha fornecidos correspondem a algum usuário
     * já cadastrado em Biblioteca.listaDeUsuarios.
     *
     * @param b a instância da Biblioteca (fonte dos dados de usuários)
     * @return o Usuario autenticado, ou null se as credenciais forem inválidas
     */
    public Usuario login(Biblioteca b) {
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();

        for (Usuario u : b.listaDeUsuarios.getLista()) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getSenha().equals(senha)) {
                System.out.println("Login realizado com sucesso! Bem-vindo(a), " + u.getNome() + ".");
                return u;
            }
        }

        System.out.println("Email ou senha inválidos.");
        return null;
    }

    /**
     * Realiza o cadastro de um novo usuário.
     * Verifica se o ID fornecido é válido consultando Biblioteca.thisIdIsValid().
     * Verifica também se o email já não está em uso.
     *
     * @param b a instância da Biblioteca
     * @return o novo Usuario cadastrado, ou null se o cadastro falhar
     */
    public Usuario cadastro(Biblioteca b) {
        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        // Verifica se o email já está cadastrado
        for (Usuario u : b.listaDeUsuarios.getLista()) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                System.out.println("Este email já está em uso.");
                return null;
            }
        }

        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();

        System.out.print("Semestre: ");
        String semestre = scanner.nextLine().trim();

        System.out.print("ID (matrícula ou ID institucional): ");
        String id = scanner.nextLine().trim();

        // Valida o ID junto ao banco de IDs da biblioteca
        if (!b.thisIdIsValid(id)) {
            System.out.println("ID inválido. Cadastro não autorizado.");
            return null;
        }

        // Determina a categoria com base no prefixo/formato do ID
        // (A lógica exata depende da convenção adotada no projeto;
        //  aqui usamos uma abordagem baseada no enum disponível em Usuario)
        Usuario.Categoria categoria = resolverCategoria(id, b);

        Usuario novoUsuario = new Usuario(id, nome, email, senha, categoria);
        b.listaDeUsuarios.adicionar(novoUsuario);

        System.out.println("Cadastro realizado com sucesso! Bem-vindo(a), " + nome + ".");
        return novoUsuario;
    }

    // -------------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------------

    /**
     * Infere a categoria do usuário a partir do ID validado.
     * Ajuste a lógica conforme a convenção de IDs do projeto.
     */
    private Usuario.Categoria resolverCategoria(String id, Biblioteca b) {
        if (b.isIdProfessor(id)) {
            return Usuario.Categoria.Professor;
        } else if (b.isIdBibliotecario(id)) {
            return Usuario.Categoria.Bibliotecario;
        } else {
            return Usuario.Categoria.Aluno;
        }
    }
}