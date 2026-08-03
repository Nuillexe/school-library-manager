package br.edu.ifba.controller.features.bibliotecario;

import br.edu.ifba.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class BottomMenuController {

    @FXML
    private void navegarParaDashboard(MouseEvent event) {
        NavigationManager.navegarPara(event, "/views/bibliotecario/dashboard.fxml");
    }

    @FXML
    private void navegarParaInventario(MouseEvent event) {
        NavigationManager.navegarPara(event, "/views/bibliotecario/inventario.fxml");
    }

    @FXML
    private void navegarParaControleDeReservas(MouseEvent event) {
        NavigationManager.navegarPara(event, "/views/bibliotecario/controleDeReservas.fxml");
    }

    @FXML
    private void navegarParaControleDeEmprestimos(MouseEvent event) {
        NavigationManager.navegarPara(event, "/views/bibliotecario/controleDeEmprestimos.fxml");
    }

}
