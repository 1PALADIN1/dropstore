import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import scenemanager.SceneManager;
import java.io.File;
import java.io.IOException;

public class LoginController {
    private SceneManager sceneManager;
    private ClientSession session;
    private CustomAlert alert;

    //контроллы
    @FXML
    TextField loginField;
    @FXML
    PasswordField passField;

    @FXML
    private void initialize() {
        try {
            session = ClientSession.getClientSession();
        } catch (IOException e) {
            alert = new CustomAlert("Не удалось создать подключение", "Ошибка", null, Alert.AlertType.ERROR);
            alert.showAlert();
            e.printStackTrace();
        }
    }

    public void login() {
        try {
            session = ClientSession.getClientSession();
            if (session != null) {
                if (session.authUser(loginField.getText(), passField.getText())) {
                    //showAlert("Поздравляем! Вы залогинились!");
                    sceneManager = new SceneManager();
                    sceneManager.changePrimaryStage("templates/filemanager.fxml", "File Manager");

                    //создание дефолтной папки для пользовательских загрузок
                    File folder = new File("download");
                    if (!folder.exists()) folder.mkdir();
                } else {
                    alert = new CustomAlert("Введены неверные логин и/или пароль", "Ошибка", null, Alert.AlertType.ERROR);
                    alert.showAlert();
                    //showAlert("Введены неверные логин и/или пароль");
                }
            } else {
                alert = new CustomAlert("Не удалось создать сессию, возможно, сервер недоступен", "Ошибка", null, Alert.AlertType.ERROR);
                alert.showAlert();
                //showAlert("Не удалось создать сессию, возможно, сервер недоступен");
            }
        } catch (IOException e) {
            e.printStackTrace();
            alert = new CustomAlert(e.getMessage(), "Ошибка", null, Alert.AlertType.ERROR);
            alert.showAlert();
            //showAlert(e.getMessage());
        } catch (CustomClientException e) {
            alert = new CustomAlert(e.getMessage(), "Ошибка", null, Alert.AlertType.ERROR);
            alert.showAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO вынести в отдельный контроллер со своим представлением
    public void registration() {
        try {
            session = ClientSession.getClientSession();
            if (session != null) {
                if (session.regUser(loginField.getText(), passField.getText())) {
                    //showAlert("Пользователь успешно зарегистрировался!");
                    sceneManager = new SceneManager();
                    sceneManager.changePrimaryStage("templates/filemanager.fxml", "File Manager");

                    //создание дефолтной папки для пользовательских загрузок
                    File folder = new File("download");
                    if (!folder.exists()) folder.mkdir();
                } else {
                    alert = new CustomAlert("Такой пользователь уже есть в системе", "Ошибка", null, Alert.AlertType.ERROR);
                    alert.showAlert();
                }
            }
        } catch (IOException | CustomClientException e) {
            e.printStackTrace();
            alert = new CustomAlert(e.getMessage(), "Ошибка", null, Alert.AlertType.ERROR);
            alert.showAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
