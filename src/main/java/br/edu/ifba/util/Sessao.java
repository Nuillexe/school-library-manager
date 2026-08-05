package br.edu.ifba.util;

import br.edu.ifba.models.Titulo;
import br.edu.ifba.models.Usuario;

public class Sessao {
    // O 'static' garante que o dado seja o mesmo para o programa inteiro
    private static Usuario usuarioLogado;
    private static Titulo tituloSelecionado;


    public static void setUsuarioLogado(Usuario usuario) {
        usuarioLogado = usuario;
    }

    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public static Titulo getTituloSelecionado() {
        return tituloSelecionado;
    }

    public static void setTituloSelecionado(Titulo tituloSelecionado) {
        Sessao.tituloSelecionado = tituloSelecionado;
    }

    // Método útil para o botão "Sair"
    public static void encerrarSessao() {
        usuarioLogado = null;
    }
}
