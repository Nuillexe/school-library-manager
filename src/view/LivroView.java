package view;

public class LivroView {

    public Scene getScene(Stage stage) {

        Label titulo = new Label("Título do Livro");
        Label autor = new Label("Autor");
        Label descricao = new Label("Descrição");

        Button emprestar = new Button("Emprestar");
        Button reservar = new Button("Reservar");

        VBox layout = new VBox(10, titulo, autor, descricao, emprestar, reservar);

        return new Scene(layout, 400, 300);
    }
}
