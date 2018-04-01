package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class LoginController {
    private Socket socket;
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 5654;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    //контроллы
    @FXML
    TextField loginField;
    @FXML
    TextField passField;

    public void login() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String msg = "/auth " + loginField.getText() + " " + passField.getText(); //временно для отладки
            dataOutputStream.writeUTF(msg);
            showAlert(dataInputStream.readUTF());

        } catch (ConnectException e) {
            showAlert("Не удалось подключиться к серверу, возможно, сервер недоступен.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataInputStream != null) dataInputStream.close();
                if (dataOutputStream != null) dataOutputStream.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
