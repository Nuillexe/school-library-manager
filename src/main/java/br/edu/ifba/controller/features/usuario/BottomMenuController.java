package br.edu.ifba.controller.features.usuario;

import br.edu.ifba.util.Tools;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class BottomMenuController {

    @FXML private void navegarParaCatalogo(MouseEvent event) {
        Tools.navegarPara(event, "/views/usuario_views/Catalogo.fxml");
    }

    @FXML private void navegarParaEmprestimos(MouseEvent event) { Tools.navegarPara(event,"/views/usuario_views/Emprestimos.fxml"); }

    @FXML private void navegarParaReservas(MouseEvent event) { Tools.navegarPara(event,"/views/usuario_views/Reservas.fxml"); }

}
