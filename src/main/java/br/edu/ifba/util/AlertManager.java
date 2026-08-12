package br.edu.ifba.util;

import javafx.scene.control.Alert;

public class AlertManager {
    private static Alert alert;

    static public void showInfo(String titleText, String headerText,String information){
        if(alert==null)
            alert= new Alert(Alert.AlertType.INFORMATION);
        else
            alert.setAlertType(Alert.AlertType.INFORMATION);

        alert.setTitle(titleText);
        alert.setHeaderText(headerText);
        alert.setContentText(information);

        alert.showAndWait();
    }

    static public void showInfo(String headerText,String information){
        if(alert==null)
            alert= new Alert(Alert.AlertType.INFORMATION);
        else
            alert.setAlertType(Alert.AlertType.INFORMATION);

        alert.setHeaderText(headerText);
        alert.setContentText(information);

        alert.showAndWait();
    }

    static public void showInfo(String information){
        if(alert==null)
            alert= new Alert(Alert.AlertType.INFORMATION);
        else
            alert.setAlertType(Alert.AlertType.INFORMATION);

        alert.setContentText(information);

        alert.showAndWait();
    }

    static public void confirmar(String text){
        if(alert==null)
            alert= new Alert(Alert.AlertType.CONFIRMATION);
        else
            alert.setAlertType(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Confirmação");
        alert.setContentText(text);

        alert.showAndWait();
    }

    static public void confirmar(String title, String text){
        if(alert==null)
            alert= new Alert(Alert.AlertType.CONFIRMATION);
        else
            alert.setAlertType(Alert.AlertType.CONFIRMATION);

        alert.setTitle(title);
        alert.setContentText(text);

        alert.showAndWait();
    }


    static public void alertar(String alerta){
        if(alert==null)
            alert= new Alert(Alert.AlertType.WARNING);
        else
            alert.setAlertType(Alert.AlertType.WARNING);

        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(alerta);
        alert.showAndWait();
    }

    static public void showError(String erro){
        if(alert==null)
            alert= new Alert(Alert.AlertType.ERROR);
        else
            alert.setAlertType(Alert.AlertType.ERROR);

        alert.setTitle("Erro");
        alert.setContentText(erro);
        alert.showAndWait();
    }

    public static Alert getAlert() {
        return alert;
    }
}
