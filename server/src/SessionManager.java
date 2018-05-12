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
    public void uploadFileOnServer(String login, String fileName, String folderId, byte[] fileBytes) throws CustomServerException, IOException, SQLException {
        //у каждого пользователя своя папка на сервере
        //для разграничения нужен login
        String userId = null;
        ResultSet rs;

        //создаём папку для корневого каталога, если ещё не создана
        File rootFolder = new File("share//" + login);
        if (!rootFolder.exists()) rootFolder.mkdirs();

        //TODO вынести в отдельный метод
        rs = dbManager.query("users", "login = ?", login);
        if (rs.next()) userId = rs.getString("id");

        //проверяем на существование в базе
        if (!dbManager.checkExistence("files", "file_name = ? and parent_dir_id = ?", fileName, folderId)) {
            //вставляем данные в таблицу\
            //TODO вставить user_id
            if (folderId.equals("root")) {
                dbManager.insert("files", new String[]{"user_id", "file_path", "file_name", "file_type"},
                        new String[]{userId, "share//" + login + "//", fileName, "1"});
            } else {
                dbManager.insert("files", new String[]{"user_id", "file_path", "file_name", "file_type", "parent_dir_id"},
                        new String[]{userId, "share//" + login + "//", fileName, "1", folderId});
            }

            //создаём сам файл
            File file;
            if (folderId.equals("root")) {
                file = new File("share//" + login + "//" + fileName);
            } else {
                file = new File("share//" + login + "//" + folderId + "_" + fileName);
            }
            if (!file.exists()) {
                file.createNewFile();
                if (folderId.equals("root")) fileOut = new FileOutputStream("share//" + login + "//" + fileName);
                else
                    fileOut = new FileOutputStream("share//" + login + "//" + folderId + "_" + fileName);
                fileOut.write(fileBytes);
            }
            if (fileOut != null) fileOut.close();
        } else {
            throw new CustomServerException("Такой файл уже существует в системе");
        }
    }

    //скачать файл с сервера
    public byte[] downloadFileFromServer(String login, String fileName, String folderId) throws Exception {
        boolean checkExist;
        String userId = null;
        ResultSet rs;

        //TODO вынести в отдельный метод
        rs = dbManager.query("users", "login = ?", login);
        if (rs.next()) userId = rs.getString("id");

        if (folderId.equals("root")) checkExist = dbManager.checkExistence("files", "user_id = ? AND file_name = ? AND parent_dir_id is null", userId, fileName);
        else
            checkExist = dbManager.checkExistence("files", "user_id = ? AND file_name = ? AND parent_dir_id = ?", userId, fileName, folderId);

        if (!checkExist) {
            //TODO заменить на кастомные
            throw new Exception("Такого файла не существует, обратитесь к администратору");
        }
        FileInputStream fileInputStream;
        if (folderId.equals("root")) fileInputStream = new FileInputStream("share//" + login + "//" + fileName);
        else
            fileInputStream = new FileInputStream("share//" + login + "//" + folderId + "_" + fileName);

        byte[] fileBytes = new byte[fileInputStream.available()];
        fileInputStream.read(fileBytes);

        return fileBytes;
    }

    public void createDirectory(String login, String dirName, String folderId) throws Exception {
        boolean checkExist;
        String userId = null;
        ResultSet rs;

        //TODO вынести в отдельный метод
        rs = dbManager.query("users", "login = ?", login);
        if (rs.next()) userId = rs.getString("id");

        if (folderId.equals("root")) checkExist = dbManager.checkExistence("files", "user_id = ? AND file_name = ? AND parent_dir_id is null", userId, dirName);
        else
            checkExist = dbManager.checkExistence("files", "user_id = ? AND file_name = ? AND parent_dir_id = ?", userId, dirName, folderId);

        if (checkExist) throw new Exception("Такая папка уже существует!");
        else {
            if (folderId.equals("root")) {
                dbManager.insert("files", new String[]{"user_id", "file_path", "file_name", "file_type"},
                        new String[]{userId, "share//" + login + "//", dirName, "2"});
            } else {
                dbManager.insert("files", new String[]{"user_id", "file_path", "file_name", "file_type", "parent_dir_id"},
                        new String[]{userId, "share//" + login + "//", dirName, "2", folderId});
            }
        }
    }

    //удаление файла с сервера и базы
    public void deleteFileFromServer(String login, String fileName, String folderId, String objectType) throws SQLException, CustomServerException {
        String userId = null;
        ResultSet rs;
        File file;

        //TODO вынести в отдельный метод
        rs = dbManager.query("users", "login = ?", login);
        if (rs.next()) userId = rs.getString("id");

        if (objectType.equals("1")) {
            //для файлов

            if (folderId.equals("root")) file = new File("share//" + login + "//" + fileName);
            else
                file = new File("share//" + login + "//" + folderId + "_" + fileName);
            if (file.exists()) {
                if (file.delete()) {
                    if (folderId.equals("root")) dbManager.delete("files", "user_id = ? AND file_name = ? AND parent_dir_id is null", userId, fileName);
                    else
                        dbManager.delete("files", "user_id = ? AND file_name = ? AND parent_dir_id = ?", userId, fileName, folderId);
                }
            } else {
                throw new CustomServerException("Не удалось найти файл на сервере");
            }
        } else {
            //для папок
            //TODO реализовать каскадное удаление файлов
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

    public String getParentFolderId(String login, String folderId) throws SQLException {
        ResultSet rs;
        if (folderId.equals("root")) return "root";

        rs = dbManager.query("users", "login = ?", login);
        if (rs.next()) {
            String userId = rs.getString("id");
            rs = dbManager.query("files", "user_id = ? and id = ?", userId, folderId);
            if (rs.next()) {
                String result = rs.getString("parent_dir_id");
                return result == null ? "root" : result;
            }
        }
        return "root";
    }

    public void close() {
        dbManager.disconnect();
    }
}
