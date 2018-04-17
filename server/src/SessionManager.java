import dbmanager.DBManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SessionManager {
    private DBManager dbManager;
    private FileOutputStream fileOut;
    private AuthService authService;

    public SessionManager(AuthService authService) {
        this.authService = authService;
        this.dbManager = authService.getDBConnection();
    }

    public void uploadFileOnServer(String login, String fileName, byte[] fileBytes) {
        //у каждого пользователя своя папка на сервере
        //для разграничения нужен login

        try {
            //создаём папку для корневого каталога
            File rootFolder = new File("share//" + login);
            if (!rootFolder.exists()) rootFolder.mkdirs();
            //создаём сам файл
            File file = new File("share//" + login + "//" + fileName);
            if (!file.exists()) {
                file.createNewFile();
                fileOut = new FileOutputStream("share//" + login + "//" + fileName);
                fileOut.write(fileBytes);

                //вставляем данные в таблицу
                dbManager.insert("files", new String[] { "user_id", "file_path", "file_name", "file_type" },
                        new String[] { "1", "share//" + login + "//", fileName, "1" });
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

    public void close() {
        dbManager.disconnect();
    }
}
