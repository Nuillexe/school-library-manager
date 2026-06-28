package br.edu.ifba.service;

import br.edu.ifba.models.Biblioteca;
import br.edu.ifba.models.Usuario;
import br.edu.ifba.enums.TipoUsuario;
import br.edu.ifba.repository.PersistenceManager;

public class AuthService {
    // Instância única da biblioteca compartilhada no sistema
    private static Biblioteca b = Biblioteca.getInstance();

    /**
     * Realiza o login do usuário.
     * Verifica se o email e a senha fornecidos correspondem a algum usuário
     * já cadastrado na lista global da biblioteca.
     *
     * @param email Email inserido na tela de login
     * @param senha Senha inserida na tela de login
     * @return o Usuario autenticado, ou null se as credenciais forem inválidas
     */
    public static Usuario login(String email, String senha) {
        // Percorre o array gerado pelo DAO customizado para verificar as credenciais
        for (Usuario u : b.getListaDeUsuarios().listar()) {
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
     * Verifica se o ID fornecido é válido e autorizado consultando b.thisIDIsValid().
     * Verifica também duplicidade de e-mail e ID para evitar inconsistência de dados.
     *
     * @return o novo Usuario cadastrado, ou null se o cadastro falhar
     */
    public static Usuario cadastro(String nome, String email, String senha, String id) {
        // Valida se o ID estrutural está presente no banco de IDs autorizados da biblioteca
        if (!b.thisIDIsValid(id)) {
            System.out.println("ID inválido ou não autorizado pelo sistema.");
            return null;
        }

        // Garante a unicidade de e-mail e ID varrendo o array do repositório
        for (Usuario u : b.getListaDeUsuarios().listar()) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                System.out.println("Este email já está em uso.");
                return null;
            }
            if (u.getId().equalsIgnoreCase(id)) {
                System.out.println("Já há no sistema um usuário cadastrado com este ID.");
                return null;
            }
        }

        // Determina o perfil com base na primeira letra do ID validado
        TipoUsuario categoria = resolverCategoria(id);

        // Instancia o novo usuário e o persiste na memória através do DAO
        Usuario novoUsuario = new Usuario(id, nome, email, senha, categoria);
        b.getListaDeUsuarios().salvar(novoUsuario);
        PersistenceManager.salvarUsuario(novoUsuario);
        System.out.println("Cadastro realizado com sucesso! Bem-vindo(a), " + nome + ".");
        return novoUsuario;
    }


    private static TipoUsuario resolverCategoria(String id) {
        if (id == null || id.isEmpty()) {
            return TipoUsuario.ALUNO;
        }

        char prefixo = id.toLowerCase().charAt(0);
        if (prefixo == 'p') {
            return TipoUsuario.PROFESSOR;
        } else if (prefixo == 'l') {
            return TipoUsuario.BIBLIOTECARIO;
        } else {
            return TipoUsuario.ALUNO; // Cobre 's' (estudantes) e qualquer outro prefixo de aluno
        }
    }
}