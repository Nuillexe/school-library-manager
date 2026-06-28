package br.edu.ifba.repository.dao.filaDeReserva;

import br.edu.ifba.models.Reserva;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class ReservaDAOFilaDePrioridade {

    private PriorityQueue<Reserva> fila;

    public ReservaDAOFilaDePrioridade() {
        this.fila = new PriorityQueue<>(new ReservaComparator());
    }

    // Adicionar reserva (enqueue)
    public void salvar(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva não pode ser nula.");
        }
        fila.add(reserva);
    }

    // Ver próxima reserva (sem remover)
    public Reserva proximo() {
        return fila.peek();
    }

    // Remover próxima reserva (dequeue)
    public Reserva removerProximo() {
        return fila.poll();
    }

    // Buscar por ID (precisa percorrer)
    public Reserva buscarPorId(long id) {
        for (Reserva r : fila) {
            if (r != null && r.getId() == id) {
                return r;
            }
        }
        return null;
    }

    // Listar todas (ordenadas por prioridade)
    public Reserva[] listar() {
        List<Reserva> lista = new ArrayList<>(fila);

        if (fila.comparator() != null) {
            lista.sort(fila.comparator());
        }

        return lista.toArray(new Reserva[0]);
    }

    // Remover por ID
    public Reserva apagar(long id) {
        Reserva encontrada = null;

        for (Reserva r : fila) {
            if (r != null && r.getId() == id) {
                encontrada = r;
                break;
            }
        }

        if (encontrada != null) {
            fila.remove(encontrada);
        }

        return encontrada;
    }

    // Atualizar reserva (remove + adiciona)
    public void atualizar(long id, Reserva novaReserva) {
        if (novaReserva == null) {
            throw new IllegalArgumentException("Reserva não pode ser nula.");
        }

        Reserva antiga = apagar(id);

        if (antiga == null) {
            throw new IllegalArgumentException("Reserva com ID " + id + " não encontrada.");
        }

        fila.add(novaReserva);
    }

    // Ver posição na fila
    public int posicao(Reserva reserva) {
        if (reserva == null) {
            return -1;
        }

        List<Reserva> lista = new ArrayList<>(fila);

        if (fila.comparator() != null) {
            lista.sort(fila.comparator());
        }

        return lista.indexOf(reserva);
    }

    // Tamanho da fila
    public int tamanho() {
        return fila.size();
    }

    // Verificar se está vazia
    public boolean estaVazia() {
        return fila.isEmpty();
    }

    // Limpar fila
    public void limpar() {
        fila.clear();
    }
}
