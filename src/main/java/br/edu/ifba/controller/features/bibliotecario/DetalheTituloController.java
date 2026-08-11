package br.edu.ifba.controller.features.bibliotecario;

import br.edu.ifba.models.Emprestimo;
import br.edu.ifba.models.Livro;
import br.edu.ifba.models.Titulo;
import br.edu.ifba.service.BibliotecarioService;
import br.edu.ifba.util.AlertManager;
import br.edu.ifba.util.Sessao;
import br.edu.ifba.util.NavigationManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;


import java.net.URL;
import java.util.ResourceBundle;

public class DetalheTituloController implements Initializable {
    @FXML
    Label lblTitulo;

    @FXML
    Label lblAutor;

    @FXML
    Label lblIsbn;

    @FXML
    Label lblTotalExemplares;

    @FXML
    Label lblDisponiveis;

    @FXML
    Label lblEmprestados;

    @FXML
    Label lblReservas;

    @FXML
    TableView<Livro> tbExemplares;

    @FXML
    TableColumn<Livro,String> colIdExemplar;

    @FXML
    TableColumn<Livro,String> colStatus;

    @FXML
    TableColumn<Livro,Void> colActions;

    private BibliotecarioService service;
    private Titulo titulo;

    public void initialize(URL location, ResourceBundle resources) {
        titulo= Sessao.getTituloSelecionado();
        this.service = new BibliotecarioService(Sessao.getUsuarioLogado());
        carregarInformacoes();
        carregarExemplaresNaTabela();
    }

    private void carregarInformacoes(){
        lblTitulo.setText(titulo.getNome());
        lblAutor.setText(titulo.getAutor());
        lblIsbn.setText(titulo.getIsbn());

        lblTotalExemplares.setText(String.valueOf(titulo.getQuantidadeDeExemplares()));
        lblDisponiveis.setText(String.valueOf(titulo.getQuantidadeDisponivel()));
        lblEmprestados.setText(String.valueOf(titulo.getQuantidadeDeExemplares()-titulo.getQuantidadeDisponivel()));
        lblReservas.setText(String.valueOf(titulo.getFilaDeReservas().tamanho()));
    }

    private void carregarExemplaresNaTabela(){
        colIdExemplar.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Livro,String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Livro,String> p){
                return new SimpleStringProperty(String.valueOf(p.getValue().getId()));
            }
        });

        colStatus.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Livro, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Livro, String> p) {
                return new SimpleStringProperty(p.getValue().isDisponivel()
                        ? "✅Disponivel"
                        : "Foi emprestado");
            }
        });

        colActions.setCellFactory(column -> new TableCell<Livro, Void>() {
            private final Button btnApagar = new Button("Apagar");

            {
                btnApagar.setOnAction(e -> {
                    Livro livroAtual = getTableView().getItems().get(getIndex());
                    if (livroAtual != null) {
                        AlertManager.confirmar("Tem certeza que deja apagar esse livro, todos os registros, e emprestimos  " +
                                "relacionados a ele, serão apagados");


                        service.removerLivro(livroAtual.getId());

                        // Atualiza os itens da tabela após remover
                        tbExemplares.getItems().remove(livroAtual);
                        carregarInformacoes();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnApagar);
                }
            }
        });


        ObservableList<Livro> list= FXCollections.observableArrayList(titulo.getListaDeExemplares().listar());
        tbExemplares.setItems(list);
        tbExemplares.getColumns().add(colActions);
    }


    @FXML
    private void selecionarLivro(MouseEvent event){

        Livro l= tbExemplares.getSelectionModel().getSelectedItem();

        if(l!=null){
            if(l.isDisponivel())
                AlertManager.showInfo(dadosDeLivroDisponivel(l));
            else
                AlertManager.showInfo(dadosDeLivroEmprestado(l));
        }
    }

    private String dadosDeLivroDisponivel(Livro l){
        return String.format("""
                Id: %d
                Data de publicação: %s                
                """,l.getId(), l.getDataPublicacao());
    }

    private String dadosDeLivroEmprestado(Livro l){
        Emprestimo emprestimoDoLivro =titulo.getListaDeEmprestimos().buscarEmprestimoBaseadoNoLivro(l);

        String nomeDoUsuario=emprestimoDoLivro.getUsuario().getNome();
        String idDoUsuario=String.valueOf(emprestimoDoLivro.getUsuario().getId());

        String status= emprestimoDoLivro.isAtrasado()?"Atrasado" : "Esta em dia ";
        String dataDoEmprestimo= emprestimoDoLivro.getDataEmprestimo().toString();
        String dataDeDevolucao= emprestimoDoLivro.getDataDevolucao().toString();

        return String.format("""
                Id: %d
                Data de publicação: %s
                Data do Emprestimo: %s
                Data de Devolução: %s
                Estado do Emprestimo: %s
                Dados do usuario que pegou o livro Emprestado:
                    -Nome: %s
                    -Id: %s            
                """,l.getId(), l.getDataPublicacao(),
                dataDoEmprestimo, dataDeDevolucao, status, nomeDoUsuario, idDoUsuario );
    }
    
    @FXML
    private void onVoltar(MouseEvent event) {
        NavigationManager.navegarPara(event, "/views/bibliotecario/inventario.fxml");
    }

}
