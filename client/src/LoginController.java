import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import scenemanager.SceneManager;
import java.io.File;
import java.io.IOException;

public class LoginController {
    private SceneManager sceneManager;
    private SessionManager session;

    //контроллы
    @FXML
    TextField loginField;
    @FXML
    TextField passField;

    public void login() {
        try {
            session = ClientApp.getSession();
            if (session != null) {
                if (session.authUser(loginField.getText(), passField.getText())) {
                    showAlert("Поздравляем! Вы залогинились!");
                    sceneManager = new SceneManager();
                    sceneManager.changePrimaryStage("templates/filemanager.fxml", "File Manager");

                    //создание дефолтной папки для пользовательских загрузок
                    File folder = new File("download");
                    if (!folder.exists()) folder.mkdir();
                } else {
                    showAlert("Введены неверные логин и/или пароль");
                }
            } else {
                showAlert("Не удалось создать сессию, возможно, сервер недоступен");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Возникли проблемы");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

}
