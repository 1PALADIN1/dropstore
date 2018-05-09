import dbmanager.DBManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionManager {
    private DBManager dbManager;
    private FileOutputStream fileOut;
    private AuthService authService;

    public SessionManager(AuthService authService) {
        this.authService = authService;
        this.dbManager = authService.getDBConnection();
    }

    public void uploadFileOnServer(String login, String fileName, String folderId, byte[] fileBytes) {
        //у каждого пользователя своя папка на сервере
        //для разграничения нужен login

        try {
            //создаём папку для корневого каталога
            File rootFolder = new File("share//" + login);
            if (!rootFolder.exists()) rootFolder.mkdirs();
            //создаём сам файл
            File file;
            if (folderId.equals("")) {
                file = new File("share//" + login + "//" + fileName);
            } else {
                file = new File("share//" + login + "//" + folderId + "_" + fileName);
            }
            if (!file.exists()) {
                file.createNewFile();
                if (folderId.equals("")) fileOut = new FileOutputStream("share//" + login + "//" + fileName);
                else
                    fileOut = new FileOutputStream("share//" + login + "//" + folderId + "_" + fileName);
                fileOut.write(fileBytes);

                //вставляем данные в таблицу
                if (folderId.equals("")) {
                    dbManager.insert("files", new String[]{"user_id", "file_path", "file_name", "file_type"},
                            new String[]{"1", "share//" + login + "//", fileName, "1"});
                } else {
                    dbManager.insert("files", new String[]{"user_id", "file_path", "file_name", "file_type", "parent_dir_id"},
                            new String[]{"1", "share//" + login + "//", fileName, "1", folderId});
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOut != null) fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteFileFromServer(String login, String filePath, String fileName) {
        if (filePath.equals("root")) filePath = "";
        File file = new File("share//" + login + "//" + filePath + fileName);
        if (file.exists()) {
            if (file.delete()) {
                dbManager.delete("files", "file_path = ? AND file_name = ?", "share//" + login + "//" + filePath, fileName);
            }
        }
    }

    public String getFileList(String login) {
        String userId = null;
        StringBuilder outStr = new StringBuilder();
        ResultSet rs;
        try {
            //получение user_id
            rs = dbManager.query("users", "login = ?", login);
            if (rs.next()) userId = rs.getString("id");

            rs = dbManager.query("files", "user_id = ?", userId);
            while (rs.next()) {
                outStr.append(rs.getString("file_name"));
                outStr.append("|");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return outStr.toString();
    }

    public void close() {
        dbManager.disconnect();
    }
}
