
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scenemanager.SceneManager;

public class ClientApp extends Application {
    private final static String SERVER_IP = "localhost";
    private final static int SERVER_PORT = 5654;
    private SceneManager sceneManager;
    private Stage primaryStage;
    private static ClientSession sessionManager;

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

    }
}
