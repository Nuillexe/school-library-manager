package br.edu.ifba.repository.dao;

import br.edu.ifba.models.Reserva;
import br.edu.ifba.models.Titulo;
import br.edu.ifba.models.Usuario;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAOLista {

    private List<Reserva> listaReservas = new ArrayList<>();

    // Salvar reserva
    public void salvar(Reserva r) {
        if (r == null) {
            throw new IllegalArgumentException("Reserva não pode ser nula.");
        }
        listaReservas.add(r);
    }

    // Listar todas reservas
    public Reserva[] listar() {

        Reserva[] array = new Reserva[listaReservas.size()];

        for (int i = 0; i < listaReservas.size(); i++) {
            array[i] = listaReservas.get(i);
        }

        return array;
    }

    // Buscar por título
    public Reserva[] buscarPorTitulo(Titulo t) {

        int contador = 0;

        for (int i = 0; i < listaReservas.size(); i++) {
            Reserva r = listaReservas.get(i);

            if (r != null && r.getTitulo() != null && r.getTitulo().equals(t)) {
                contador++;
            }
        }

        Reserva[] resultado = new Reserva[contador];
        int j = 0;

        for (int i = 0; i < listaReservas.size(); i++) {
            Reserva r = listaReservas.get(i);

            if (r != null && r.getTitulo() != null && r.getTitulo().equals(t)) {
                resultado[j++] = r;
            }
        }

        return resultado;
    }

    // Buscar por usuário
    public Reserva[] buscarPorUsuario(Usuario u) {

        int contador = 0;

        for (int i = 0; i < listaReservas.size(); i++) {
            Reserva r = listaReservas.get(i);

            if (r != null && r.getUsuario() != null && r.getUsuario().equals(u)) {
                contador++;
            }
        }

        Reserva[] resultado = new Reserva[contador];
        int j = 0;

        for (int i = 0; i < listaReservas.size(); i++) {
            Reserva r = listaReservas.get(i);

            if (r != null && r.getUsuario() != null && r.getUsuario().equals(u)) {
                resultado[j++] = r;
            }
        }

        return resultado;
    }

    // Atualizar reserva
    public void atualizar(long id, Reserva nova) {

        for (int i = 0; i < listaReservas.size(); i++) {

            Reserva r = listaReservas.get(i);

            if (r != null && r.getId() == id) {
                listaReservas.set(i, nova);
                return;
            }
        }

        throw new IllegalArgumentException("Reserva com ID " + id + " não encontrada.");
    }

    // Apagar reserva
    public Reserva apagar(long id) {

        for (int i = 0; i < listaReservas.size(); i++) {

            Reserva r = listaReservas.get(i);

            if (r != null && r.getId() == id) {
                return listaReservas.remove(i);
            }
        }

        return null;
    }
    public int tamanho() {
        return listaReservas.size();
    }

    public void limpar(){
        listaReservas.clear();
    }
}
