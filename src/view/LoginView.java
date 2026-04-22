package view;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginView {

    public void start(Stage stage) {

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField senhaField = new PasswordField();
        senhaField.setPromptText("Senha");

        Button btnLogin = new Button("Entrar");

        btnLogin.setOnAction(e -> {
            String email = emailField.getText();
            String senha = senhaField.getText();

            // chamar controller aqui
            System.out.println("Login: " + email);
        });

        VBox layout = new VBox(10, emailField, senhaField, btnLogin);

        Scene scene = new Scene(layout, 300, 200);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }
}
