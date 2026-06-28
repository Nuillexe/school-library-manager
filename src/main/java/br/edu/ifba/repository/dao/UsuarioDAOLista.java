package br.edu.ifba.repository.dao;

import br.edu.ifba.models.Usuario;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOLista {

    private List<Usuario> listaDeUsuarios = new ArrayList<>();

    //  Salvar usuário
    public void salvar(Usuario u) {
        if (u == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo.");
        }
        listaDeUsuarios.add(u);
    }

    //  Buscar por ID
    public Usuario buscarPorId(String id) {
        for (int i = 0; i < listaDeUsuarios.size(); i++) {
            Usuario u = listaDeUsuarios.get(i);
            if (u.getId().equals(id)) {
                return u;
            }
        }
        return null;
    }

    //  Listar todos
    public Usuario[] listar() {
        Usuario[] arrayRetorno = new Usuario[listaDeUsuarios.size()];

        for (int i = 0; i < listaDeUsuarios.size(); i++) {
            arrayRetorno[i] = listaDeUsuarios.get(i);
        }

        return arrayRetorno;
    }

    //  Atualizar usuário pelo ID
    public void atualizar(String id, Usuario usuarioAtualizado) {
        for (int i = 0; i < listaDeUsuarios.size(); i++) {
            Usuario u = listaDeUsuarios.get(i);

            if (u.getId().equals(id)) {
                listaDeUsuarios.set(i, usuarioAtualizado);
                return;
            }
        }

        throw new IllegalArgumentException("Usuário com ID " + id + " não encontrado.");
    }

    //  Apagar usuário pelo ID
    public Usuario apagar(String id) {
        for (int i = 0; i < listaDeUsuarios.size(); i++) {
            Usuario u = listaDeUsuarios.get(i);

            if (u.getId().equals(id)) {
                return listaDeUsuarios.remove(i);
            }
        }
        return null;
    }
}
