package br.edu.ifba.repository.dao;

import br.edu.ifba.models.Usuario;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOLista {

    private List<Usuario> listaDeUsuarios = new ArrayList<>();

    public void salvar(Usuario u) {
        if (u == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo.");
        }
        listaDeUsuarios.add(u);
    }

    public Usuario buscarPorId(String id) {
        for (int i = 0; i < listaDeUsuarios.size(); i++) {
            Usuario u = listaDeUsuarios.get(i);
            if (u.getId().equals(id)) {
                return u;
            }
        }
        return null;
    }

    public Usuario[] listar() {
        Usuario[] arrayRetorno = new Usuario[listaDeUsuarios.size()];

        for (int i = 0; i < listaDeUsuarios.size(); i++) {
            arrayRetorno[i] = listaDeUsuarios.get(i);
        }

        return arrayRetorno;
    }

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

    public Usuario apagar(String id) {
        for (int i = 0; i < listaDeUsuarios.size(); i++) {
            Usuario u = listaDeUsuarios.get(i);

            if (u.getId().equals(id)) {
                return listaDeUsuarios.remove(i);
            }
        }
        return null;
    }

    public void limpar(){
        listaDeUsuarios.clear();
    }
}
