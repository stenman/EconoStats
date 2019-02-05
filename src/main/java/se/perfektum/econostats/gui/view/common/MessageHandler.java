package se.perfektum.econostats.gui.view.common;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class MessageHandler {

    public static void showError(String title, String headerText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);

        alert.showAndWait();
    }

    public static void showWarning(String title, String headerText, String contentText){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    public static ButtonType showYesNoDialog(String headerText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, headerText, ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        return alert.getResult();
    }
}
