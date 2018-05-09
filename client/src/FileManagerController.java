import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.ArrayList;

public class FileManagerController {
    private SessionManager session;
    private ArrayList<ListItem> fileList;

    @FXML
    TextArea textArea;

    public void getLS() {
        session = ClientApp.getSession();
        if (session != null) {
            fileList = new ArrayList<>();
            String[] lsFiles = session.getLS("root");
            for (int i = 0; i < lsFiles.length; i++) {
                fileList.add(new ListItem(lsFiles[i], lsFiles[++i], lsFiles[++i], lsFiles[++i]));
            }

            textArea.clear();
            for (int i = 0; i < fileList.size(); i++) {
                textArea.appendText(fileList.get(i).getId() + "\t" + fileList.get(i).getName() + "\t" +
                        fileList.get(i).getType() + "\t" + fileList.get(i).getParentId() + "\n");
                //textArea.appendText(fileList.get(i).getName() + "\n");
            }
        }
    }

    public void sendFileToServer() {
        session = ClientApp.getSession();
        if (session != null) {
            session.sendFileToServer("test111.mdm");
        }
    }

    public void downloadFileFromServer() {
        session = ClientApp.getSession();
        if (session != null) {
            try {
                session.downloadFileFromServer("1234.txt", "2");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteFile() {
        session = ClientApp.getSession();
        if (session != null) {
            session.deleteFileFromServer("root", "test.txt");
        }
    }
}
