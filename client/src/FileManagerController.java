import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class FileManagerController {
    private SessionManager session;

    @FXML
    TextArea textArea;

    public void getLS() {
        session = ClientApp.getSession();
        if (session != null) {
            String[] lsFiles = session.getLS("root");
            textArea.clear();
            for (int i = 0; i < lsFiles.length; i++) {
                textArea.appendText(lsFiles[i] + "\n");
            }
        }
    }

    public void sendFileToServer() {
        session = ClientApp.getSession();
        if (session != null) {
            session.sendFileToServer("test.txt");
        }
    }
}
