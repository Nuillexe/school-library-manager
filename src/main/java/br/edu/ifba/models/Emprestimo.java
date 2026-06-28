package br.edu.ifba.models;

import br.edu.ifba.enums.TipoUsuario;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Classe responsável por registrar o empréstimo de um livro para um determinado usuário.
 */
public class Emprestimo {
    private long id;
    private static long idCount = 0; // Contador estático para geração de identificador único da transação
    private Usuario usuario;
    private Livro livro;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucao;
    private boolean atrasado;

    // Fuso horário fixo padronizado para evitar falhas de fuso em servidores locais ou nuvem
    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");

    /**
     * Construtor padrão que inicializa apenas a data atual de criação do registro.
     */
    public Emprestimo() {
        this.dataEmprestimo = LocalDate.now(ZONA);
    }

    /**
     * Construtor principal para geração automática e cálculo dos prazos da locação.
     */
    public Emprestimo(Usuario usuario, Livro livro) {
        this.id = ++idCount; // Gera automaticamente o ID incremental
        this.usuario = usuario;
        this.livro = livro;
        this.dataEmprestimo = LocalDate.now(ZONA);

        // Aplica a regra de negócio para prazos de devolução diferenciados
        if (usuario != null && usuario.getTipo() == TipoUsuario.ALUNO) {
            this.dataDevolucao = this.dataEmprestimo.plusDays(7);   // Alunos possuem 7 dias de prazo
        } else {
            this.dataDevolucao = this.dataEmprestimo.plusDays(10);  // Professores e técnicos possuem 10 dias
        }

        this.atrasado = false; // Inicia em dia
    }

    public Emprestimo(long id,
                      Usuario usuario,
                      Livro livro,
                      LocalDate dataEmprestimo,
                      LocalDate dataDevolucao,
                      boolean atrasado) {

        this.id = id;

        if (id > idCount) {
            idCount = id;
        }

        this.usuario = usuario;
        this.livro = livro;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucao = dataDevolucao;
        this.atrasado = atrasado;
    }

    // Getters e Setters

    public long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    /**
     * Método dinâmico que recalcula e atualiza o estado de atraso comparando a data atual com o prazo final.
     */
    public boolean isAtrasado() {
        // Se ainda estiver marcado como false mas a data de hoje passou do prazo limite, atualiza o status
        if (!atrasado && dataDevolucao != null && LocalDate.now(ZONA).isAfter(dataDevolucao)) {
            this.atrasado = true;
        }
        return atrasado;
    }

    public void setAtrasado(boolean atrasado) {
        this.atrasado = atrasado;
    }

    /**
     * Exibe os dados formatados e detalhados referentes ao empréstimo no terminal do sistema.
     */
    public void mostrarDados() {
        System.out.println("Empréstimo ID: " + id +
                " | Livro: " + (livro != null ? livro.getNome() : "N/A") +
                " | Usuário: " + (usuario != null ? usuario.getNome() : "N/A") +
                " | Data Empréstimo: " + dataEmprestimo +
                " | Data Devoclução: " + dataDevolucao +
                " | Status: " + (isAtrasado() ? "⚠️ ATRASADO" : "✅ EM DIA"));
    }
}