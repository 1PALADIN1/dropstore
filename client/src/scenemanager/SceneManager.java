package scenemanager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        SceneManager.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void changePrimaryStage(String template, String title) throws Exception {
        //template
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(template));
        primaryStage.setTitle(title);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }
}
