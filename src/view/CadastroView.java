package view;

public class CatalogoView {

    public Scene getScene(Stage stage) {

        TextField busca = new TextField();
        busca.setPromptText("Buscar livro...");

        ListView<String> listaLivros = new ListView<>();

        Button btnEmprestar = new Button("Emprestar");
        Button btnReservar = new Button("Reservar");

        btnEmprestar.setOnAction(e -> {
            System.out.println("Emprestar livro");
        });

        VBox layout = new VBox(10, busca, listaLivros, btnEmprestar, btnReservar);

        return new Scene(layout, 400, 400);
    }
}
