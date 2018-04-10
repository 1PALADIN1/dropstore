import dbmanager.DBManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SessionManager {
    private DBManager dbManager;
    private FileOutputStream fileOut;

    public SessionManager() {
        dbManager = new DBManager();
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
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        dbManager.disconnect();
    }
}
