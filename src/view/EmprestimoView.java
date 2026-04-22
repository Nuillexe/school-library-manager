package view;

public class EmprestimosView {

    public Scene getScene(Stage stage) {

        ListView<String> lista = new ListView<>();

        Button devolver = new Button("Devolver");

        devolver.setOnAction(e -> {
            System.out.println("Devolver livro");
        });

        VBox layout = new VBox(10, lista, devolver);

        return new Scene(layout, 400, 300);
    }
}
