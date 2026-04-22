package view;

public class ReservasView {

    public Scene getScene(Stage stage) {

        ListView<String> lista = new ListView<>();

        Label posicao = new Label("Posição na fila: ");

        VBox layout = new VBox(10, lista, posicao);

        return new Scene(layout, 400, 300);
    }
}