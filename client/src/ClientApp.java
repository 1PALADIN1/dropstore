
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("templates/login.fxml"));
        primaryStage.setTitle("DropStore GUI Client");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }
}
