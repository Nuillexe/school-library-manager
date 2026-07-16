package br.edu.ifba.controller.features.bibliotecario;

import br.edu.ifba.util.Tools;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class BottomMenuController {

    @FXML
    private void navegarParaDashboard(MouseEvent event) {
        Tools.navegarPara(event, "/views/bibliotecario_views/dashboard.fxml");
    }

    @FXML
    private void navegarParaInventario(MouseEvent event) {
        Tools.navegarPara(event, "/views/bibliotecario_views/inventario.fxml");
    }

    @FXML
    private void navegarParaControleDeReservas(MouseEvent event) {
        Tools.navegarPara(event, "/views/bibliotecario_views/controleDeReservas.fxml");
    }

    @FXML
    private void navegarParaControleDeEmprestimos(MouseEvent event) {
        Tools.navegarPara(event, "/views/bibliotecario_views/controleDeEmprestimos.fxml");
    }

}
