import dbmanager.DBManager;

import java.io.*;
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

    //загрузка файла на сервер
    public void uploadFileOnServer(String login, String fileName, String folderId, byte[] fileBytes) {
        //у каждого пользователя своя папка на сервере
        //для разграничения нужен login

        try {
            //создаём папку для корневого каталога, если ещё не создана
            File rootFolder = new File("share//" + login);
            if (!rootFolder.exists()) rootFolder.mkdirs();

            //проверяем на существование в базе
            if (!dbManager.checkExistence("files", "file_name = ? and parent_dir_id = ?", fileName, folderId)) {
                //вставляем данные в таблицу\
                //TODO вставить user_id
                if (folderId.equals("")) {
                    dbManager.insert("files", new String[]{"user_id", "file_path", "file_name", "file_type"},
                            new String[]{"1", "share//" + login + "//", fileName, "1"});
                } else {
                    dbManager.insert("files", new String[]{"user_id", "file_path", "file_name", "file_type", "parent_dir_id"},
                            new String[]{"1", "share//" + login + "//", fileName, "1", folderId});
                }

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
                }
            } else {
                //TODO кастомная ошибка
                throw new IOException("Такой файл уже существует в системе");
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

    //скачать файл с сервера
    public byte[] downloadFileFromServer(String login, String fileName, String folderId) throws Exception {
        boolean checkExist;
        String userId = null;
        ResultSet rs;

        rs = dbManager.query("users", "login = ?", login);
        if (rs.next()) userId = rs.getString("id");

        if (folderId.equals("root")) checkExist = dbManager.checkExistence("files", "user_id = ? and file_name = ? and parent_dir_id is null", userId, fileName);
        else
            checkExist = dbManager.checkExistence("files", "user_id = ? and file_name = ? and parent_dir_id = ?", userId, fileName, folderId);

        if (!checkExist) {
            //TODO заменить на кастомные
            throw new Exception("Такого файла не существует, обратитесь к администратору");
        }
        FileInputStream fileInputStream = null;
        fileInputStream = new FileInputStream("share//" + login + "//" + folderId + "_" + fileName);
        byte[] fileBytes = new byte[fileInputStream.available()];
        fileInputStream.read(fileBytes);

        return fileBytes;
    }

    //удаление файла с сервера и базы
    public void deleteFileFromServer(String login, String filePath, String fileName) {
        if (filePath.equals("root")) filePath = "";
        File file = new File("share//" + login + "//" + filePath + fileName);
        if (file.exists()) {
            if (file.delete()) {
                dbManager.delete("files", "file_path = ? AND file_name = ?", "share//" + login + "//" + filePath, fileName);
            }
        }
    }

    //получение списка файлов
    public String getFileList(String login, String folderId) {
        String userId = null;
        StringBuilder outStr = new StringBuilder();
        ResultSet rs;
        try {
            //получение user_id
            rs = dbManager.query("users", "login = ?", login);
            if (rs.next()) userId = rs.getString("id");

            if (folderId.equals("root")) rs = dbManager.query("files", "user_id = ? and parent_dir_id is null", userId);
            else
                rs = dbManager.query("files", "user_id = ? and parent_dir_id = ?", userId, folderId);
            while (rs.next()) {
                outStr.append(rs.getString("id"));
                outStr.append("|");
                outStr.append(rs.getString("file_name"));
                outStr.append("|");
                outStr.append(rs.getString("file_type"));
                outStr.append("|");
                outStr.append(rs.getString("parent_dir_id"));
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
