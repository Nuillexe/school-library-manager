package br.edu.ifba.models;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Classe responsável pelo registro de intenção de reserva quando todos os exemplares estão emprestados.
 */
public class Reserva {
    private Usuario usuario;
    private Titulo titulo;
    private LocalDateTime dataReserva;
    private static long idCount = 0;
    private long id;
    private static final long Maximo_Reservas = 1_000_000; // Limite de transações de segurança no sistema

    // Fuso horário fixo padrão da aplicação
    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");

    /**
     * Construtor padrão que inicializa o carimbo de data e hora atuais.
     */
    public Reserva() {
        this.dataReserva = LocalDateTime.now(ZONA);
    }

    /**
     * Construtor parametrizado que valida o estouro de capacidade máxima do sistema antes de instanciar a reserva.
     */
    public Reserva(Usuario usuario, Titulo titulo) {
        if (idCount == Maximo_Reservas) {
            throw new IllegalStateException("Limite máximo de reservas atingido");
        }

        this.usuario = usuario;
        this.titulo = titulo;
        this.dataReserva = LocalDateTime.now(ZONA);
        this.id = ++idCount; // Atribui ID auto-incremental único
    }

    public Reserva(int id, Usuario usuario, Titulo titulo,
                   LocalDateTime dataReserva) {

        this.id = id;

        if (id > idCount) {
            idCount = id;
        }

        this.usuario = usuario;
        this.titulo = titulo;
        this.dataReserva = dataReserva;
    }

    // Getters e Setters

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Titulo getTitulo() {
        return titulo;
    }

    public void setTitulo(Titulo titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(LocalDateTime dataReserva) {
        this.dataReserva = dataReserva;
    }

    public long getId() {
        return id;
    }

    /**
     * Imprime de forma formatada as principais propriedades da reserva no terminal.
     */
    public void mostrarDados() {
        System.out.println("Reserva efetuada em: " + dataReserva);
        if (usuario != null) System.out.println("Usuário: " + usuario.getNome());
        if (titulo != null && titulo.getNome() != null) System.out.println("Título: " + titulo.getNome());
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", usuario=" + (usuario != null ? usuario.getNome() : "N/A") +
                ", titulo=" + (titulo != null ? titulo.getNome() : "N/A") +
                ", dataReserva=" + dataReserva +
                '}';
    }
}