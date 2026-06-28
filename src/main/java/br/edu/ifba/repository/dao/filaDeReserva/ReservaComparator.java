package br.edu.ifba.repository.dao.filaDeReserva;

import br.edu.ifba.models.Reserva;
import br.edu.ifba.enums.TipoUsuario;

import java.util.Comparator;

public class ReservaComparator implements Comparator<Reserva> {

    @Override
    public int compare(Reserva r1, Reserva r2) {

        // PROFESSOR tem prioridade sobre ALUNO
        if (r1.getUsuario().getTipo() == TipoUsuario.PROFESSOR &&
                r2.getUsuario().getTipo() != TipoUsuario.PROFESSOR) {
            return -1;
        }

        if (r1.getUsuario().getTipo() != TipoUsuario.PROFESSOR &&
                r2.getUsuario().getTipo() == TipoUsuario.PROFESSOR) {
            return 1;
        }

        // ordena pela data da reserva (mais antiga primeiro)
        return r1.getDataReserva().compareTo(r2.getDataReserva());
    }
}