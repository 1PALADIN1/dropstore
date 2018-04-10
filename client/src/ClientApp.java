
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scenemanager.SceneManager;

import java.io.IOException;

public class ClientApp extends Application {
    private final static String SERVER_IP = "localhost";
    private final static int SERVER_PORT = 5654;
    private SceneManager sceneManager;
    private Stage primaryStage;
    private static SessionManager sessionManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.sceneManager = new SceneManager();
        sceneManager.setPrimaryStage(primaryStage);

        Parent root = FXMLLoader.load(getClass().getResource("templates/login.fxml"));
        primaryStage.setTitle("DropStore GUI Client");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    @Override
    public void init() {
        //инициализаци подключения
        //TODO сделать обработку ошибок отдельным классом
        try {
            sessionManager = new SessionManager(SERVER_IP, SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SessionManager getSession() throws IOException {
        //при повторной попытке подключения
        if (sessionManager == null) sessionManager = new SessionManager(SERVER_IP, SERVER_PORT);
        return sessionManager;
    }
}
