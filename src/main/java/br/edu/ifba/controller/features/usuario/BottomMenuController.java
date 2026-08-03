package br.edu.ifba.controller.features.usuario;

import br.edu.ifba.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class BottomMenuController {

    @FXML private void navegarParaCatalogo(MouseEvent event) {
        NavigationManager.navegarPara(event, "/views/usuario/Catalogo.fxml");
    }

    @FXML private void navegarParaEmprestimos(MouseEvent event) { NavigationManager.navegarPara(event, "/views/usuario/Emprestimos.fxml"); }

    @FXML private void navegarParaReservas(MouseEvent event) { NavigationManager.navegarPara(event, "/views/usuario/Reservas.fxml"); }


}
