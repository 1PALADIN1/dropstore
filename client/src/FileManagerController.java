import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.ArrayList;

public class FileManagerController {
    private SessionManager session;
    private ArrayList<ListItem> fileList;
    private ObservableList<ListItem> usersData = FXCollections.observableArrayList();

    @FXML
    TextArea textArea;

    @FXML
    private Label infoLabel;

    @FXML
    private TableView<ListItem> fileTable;

    @FXML
    private TableColumn<ListItem, String> idColumn;

    @FXML
    private TableColumn<ListItem, String> typeColumn;

    @FXML
    private TableColumn<ListItem, String> nameColumn;

    @FXML
    private TableColumn<ListItem, String> parentColumn;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        parentColumn.setCellValueFactory(new PropertyValueFactory<>("parentId"));

        session = ClientApp.getSession();
        if (session != null) {
            getLS();
        }
    }


    public void getLS() {
        session = ClientApp.getSession();
        if (session != null) {
            fileList = new ArrayList<>();
            String[] lsFiles = session.getLS("3");
            for (int i = 0; i < lsFiles.length; i++) {
                fileList.add(new ListItem(lsFiles[i], lsFiles[++i], lsFiles[++i], lsFiles[++i]));
            }

            textArea.clear();
            usersData.clear();
            if (!fileList.get(0).getParentId().equals("null")) {
                textArea.appendText("0\t..\t2\tnull\n");
                usersData.add(new ListItem("0", "..", "2", "null"));
            }
            for (int i = 0; i < fileList.size(); i++) {
                textArea.appendText(fileList.get(i).getId() + "\t" + fileList.get(i).getName() + "\t" +
                        fileList.get(i).getType() + "\t" + fileList.get(i).getParentId() + "\n");
                //textArea.appendText(fileList.get(i).getName() + "\n");

                //таблица
                usersData.add(new ListItem(fileList.get(i).getId(), fileList.get(i).getName(), fileList.get(i).getType(), fileList.get(i).getParentId()));
            }

            fileTable.setItems(usersData);
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

    public void createDirectory() {
        session = ClientApp.getSession();
        if (session != null) {
            try {
                session.createDirectory("myDir");
            } catch (IOException e) {
                System.out.println(e.getMessage());
                //e.printStackTrace();
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
