import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class CustomAlert {
    private static CustomAlert customAlert;
    private Alert alert;
    private String message;
    private String title;
    private String headerText;
    private Alert.AlertType type;

    public CustomAlert(String message, String title, String headerText, Alert.AlertType type) {
        alert = new Alert(type);
        setAlertParams(message, title, headerText, type);
    }

    public void setAlertParams(String message, String title, String headerText, Alert.AlertType type) {
        this.message = message;
        this.title = title;
        this.headerText = headerText;
        this.type = type;
        this.alert.setTitle(title);
        this.alert.setHeaderText(headerText);
        this.alert.setContentText(message);
    }

    public void showSimpleAlert() {
        Platform.runLater(() -> {
            alert.showAndWait();
        });
    }

    public ButtonType showAlert() {
        Optional<ButtonType> result = alert.showAndWait();
        return result.get();
    }
}
