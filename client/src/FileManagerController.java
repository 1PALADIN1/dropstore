import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import scenemanager.SceneManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class FileManagerController {
    private ClientSession session;
    private SceneManager sceneManager;
    private ArrayList<ListItem> fileList;
    private ObservableList<ListItem> usersData = FXCollections.observableArrayList();
    private CustomAlert alert;

    @FXML
    TextArea textArea;

    @FXML
    private Label infoLabel;

    @FXML
    private Button buttonRefresh;
    @FXML
    private Button buttonUpload;
    @FXML
    private Button buttonDownload;
    @FXML
    private Button buttonDelete;
    @FXML
    private Button buttonNewFolder;
    @FXML
    private Button buttonOpenDirectory;
    @FXML
    private Button buttonToParentDirectory;

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
    private TableColumn<ListItem, ImageView> imageColumn;

    @FXML
    private void initialize() {
        Image imageRefresh = new Image(getClass().getResourceAsStream("/res/refresh.png"));
        Image imageUpload = new Image(getClass().getResourceAsStream("/res/upload.png"));
        Image imageDownload = new Image(getClass().getResourceAsStream("/res/download.png"));
        Image imageDelete = new Image(getClass().getResourceAsStream("/res/delete.png"));
        Image imageNewFolder = new Image(getClass().getResourceAsStream("/res/new_folder.png"));
        Image imageOpenFolder = new Image(getClass().getResourceAsStream("/res/open_folder.png"));
        Image imageParentFolder = new Image(getClass().getResourceAsStream("/res/parent_folder.png"));
        buttonRefresh.setGraphic(new ImageView(imageRefresh));
        buttonUpload.setGraphic(new ImageView(imageUpload));
        buttonDownload.setGraphic(new ImageView(imageDownload));
        buttonDelete.setGraphic(new ImageView(imageDelete));
        buttonNewFolder.setGraphic(new ImageView(imageNewFolder));
        buttonOpenDirectory.setGraphic(new ImageView(imageOpenFolder));
        buttonToParentDirectory.setGraphic(new ImageView(imageParentFolder));

        //idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        //parentColumn.setCellValueFactory(new PropertyValueFactory<>("parentId"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imageView"));
        typeColumn.setVisible(false);
        fileTable.setEditable(false);

        try {
            session = ClientSession.getClientSession();
            getLS();
            infoLabel.setText("Вы успешно авторизовались");
        } catch (IOException e) {
            alert = new CustomAlert("Не удалось подключиться к серверу", "Ошибка", null, Alert.AlertType.ERROR);
            alert.showSimpleAlert();
            e.printStackTrace();
        }
    }

    public void tableMouseClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            TablePosition tablePosition = fileTable.getSelectionModel().getSelectedCells().get(0);
            int row = tablePosition.getRow();
            ListItem item = fileTable.getItems().get(row);

            if (item.getType().equals("2")) {
                if (item.getName().equals("..")) toParentDirectory();
                else
                    openDirectory();
            }
        }
    }

    public void getLS() {
        fileList = new ArrayList<>();
        String[] lsFiles = new String[0];
        try {
            lsFiles = session.getLS(session.getCurrentFolderId());
        } catch (IOException e) {
            alert = new CustomAlert(e.getMessage(), "Ошибка", null, Alert.AlertType.ERROR);
            alert.showSimpleAlert();
            e.printStackTrace();
        }
        for (int i = 0; i < lsFiles.length; i++) {
            if (i + 3 < lsFiles.length) {
                Image image;
                if (lsFiles[i + 2].equals("2")) image = new Image(getClass().getResourceAsStream("/res/folder.png"));
                else
                    image = new Image(getClass().getResourceAsStream("/res/file.png"));
                fileList.add(new ListItem(lsFiles[i], lsFiles[++i], lsFiles[++i], lsFiles[++i], new ImageView(image)));
            }
        }

        //textArea.clear();
        usersData.clear();

        if (fileList.size() == 0) {
            if (!session.getCurrentFolderId().equals("root")) {
                //textArea.appendText("0\t..\t2\tnull\n");
                usersData.add(new ListItem("0", "..", "2", session.getCurrentFolderId(), new ImageView(new Image(getClass().getResourceAsStream("/res/up_folder.png")))));
            }
        } else {
            if (!fileList.get(0).getParentId().equals("null")) {
                //textArea.appendText("0\t..\t2\tnull\n");
                usersData.add(new ListItem("0", "..", "2", session.getCurrentFolderId(), new ImageView(new Image(getClass().getResourceAsStream("/res/up_folder.png")))));
            }
        }
        for (int i = 0; i < fileList.size(); i++) {
            //textArea.appendText(fileList.get(i).getId() + "\t" + fileList.get(i).getName() + "\t" +
            //        fileList.get(i).getType() + "\t" + fileList.get(i).getParentId() + "\n");
            //textArea.appendText(fileList.get(i).getName() + "\n");

            //таблица
            usersData.add(new ListItem(fileList.get(i).getId(), fileList.get(i).getName(), fileList.get(i).getType(), fileList.get(i).getParentId(), fileList.get(i).getImageView()));
        }

        typeColumn.setSortable(true);
        typeColumn.setSortType(TableColumn.SortType.DESCENDING);
        fileTable.setItems(usersData);
        fileTable.getSortOrder().add(typeColumn);
        typeColumn.setSortable(false);
        nameColumn.setSortable(false);
    }

    //отуркытие выбранной категории
    public void openDirectory() {
        TablePosition tablePosition = fileTable.getSelectionModel().getSelectedCells().get(0);
        int row = tablePosition.getRow();
        ListItem item = fileTable.getItems().get(row);
        //System.out.println("Type: " + item.getType() + ", ID: " + item.getId() + ", PARENT_FOLDER: " + item.getParentId());

        //если выбрана папка
        if (item.getType().equals("2")) {
            if (item.getName().equals("..")) {
                toParentDirectory();
            } else {
                session.setParentFolderId(session.getCurrentFolderId());
                session.setCurrentFolderId(item.getId());
                getLS();
                if (session.getCurrentFolderId().equals("root")) infoLabel.setText("ROOT folder");
                else
                    infoLabel.setText("");
            }
        }
    }

    public void toParentDirectory() {
        try {
            session.setCurrentFolderId(session.getParentFolderId());
            session.setParentFolderId(session.getServerParentFolderId(session.getParentFolderId()));
            getLS();
            if (session.getCurrentFolderId().equals("root")) infoLabel.setText("ROOT folder");
            else
                infoLabel.setText("");
        } catch (IOException | CustomClientException e) {
            alert = new CustomAlert(e.getMessage(), "Ошибка", null, Alert.AlertType.ERROR);
            alert.showSimpleAlert();
            e.printStackTrace();
        }
    }

    public void sendFileToServer(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберете файл");
        File chooseFile = fileChooser.showOpenDialog(((Node)actionEvent.getSource()).getScene().getWindow());

        if (chooseFile != null) {
            try {
                session.sendFileToServer(chooseFile.getName(), session.getCurrentFolderId(), chooseFile);
                getLS();
            } catch (IOException | CustomClientException e) {
                alert = new CustomAlert(e.getMessage(), "Ошибка", null, Alert.AlertType.ERROR);
                alert.showSimpleAlert();
                e.printStackTrace();
            }
        }
    }

    public void downloadFileFromServer() {
        try {
            TablePosition tablePosition = fileTable.getSelectionModel().getSelectedCells().get(0);
            int row = tablePosition.getRow();
            ListItem item = fileTable.getItems().get(row);

            if (item.getType().equals("1")) {
                session.downloadFileFromServer(item.getName(), session.getCurrentFolderId());
            } else {
                alert = new CustomAlert("Скачать с сервера можно только файл", "Ошибка", null, Alert.AlertType.ERROR);
                alert.showSimpleAlert();
            }
        } catch (Exception e) {
            alert = new CustomAlert(e.getMessage(), "Ошибка", null, Alert.AlertType.ERROR);
            alert.showSimpleAlert();
            System.out.println(e.getMessage());
            //e.printStackTrace();
        }
    }

    public void createDirectory() {
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Новая папка");
            dialog.setHeaderText(null);
            dialog.setContentText("Название:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !result.toString().isEmpty()){
                System.out.println("Новая папка: " + result.get());
                session.createDirectory(result.get(), session.getCurrentFolderId());
                getLS();
            }

        } catch (IOException e) {
            alert = new CustomAlert(e.getMessage(), "Ошибка", null, Alert.AlertType.ERROR);
            alert.showSimpleAlert();

            System.out.println(e.getMessage());
            //e.printStackTrace();
        } catch (CustomClientException e) {
            alert = new CustomAlert(e.getMessage(), "Ошибка", null, Alert.AlertType.ERROR);
            alert.showSimpleAlert();
            e.printStackTrace();
        }
    }

    public void deleteFile() {
        TablePosition tablePosition = fileTable.getSelectionModel().getSelectedCells().get(0);
        int row = tablePosition.getRow();
        ListItem item = fileTable.getItems().get(row);

        if (item.getType().equals("2")) {
            alert = new CustomAlert("Вы точно хотите удалить папку и всё её содержимое?", "Удаление папки", null, Alert.AlertType.CONFIRMATION);
        } else {
            alert = new CustomAlert("Вы точно хотите удалить файл?", "Удаление файла", null, Alert.AlertType.CONFIRMATION);
        }
        if (alert.showAlert() == ButtonType.OK) {
            try {
                session.deleteFileFromServer(item.getName(), session.getCurrentFolderId(), item.getType());
                getLS();
            } catch (IOException | CustomClientException e) {
                alert = new CustomAlert(e.getMessage(), "Ошибка", null, Alert.AlertType.ERROR);
                alert.showSimpleAlert();
            }
        } else {
            System.out.println("Отмена удаления папки");
        }
    }
}
