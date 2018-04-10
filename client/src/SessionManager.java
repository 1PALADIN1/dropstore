import scenemanager.SceneManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class SessionManager {
    //класс для управления клиентской сессией
    private Socket session;
    private String serverIp;
    private int serverPort;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public SessionManager(String serverIp, int serverPort) throws IOException {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        openConnection();
    }

    private void openConnection() throws IOException {
        if (session == null || session.isClosed()) {
            session = new Socket(serverIp, serverPort);
            dataInputStream = new DataInputStream(session.getInputStream());
            dataOutputStream = new DataOutputStream(session.getOutputStream());
        }
    }

    public boolean authUser(String login, String password) throws IOException {
        String msg = "/auth " + login + " " + password; //временно для отладки
        dataOutputStream.writeUTF(msg);
        msg = dataInputStream.readUTF();

        //запрос на авторизацию
        switch (msg) {
            //временные заглушки
            case "/authok": return true;
            case "/autherror": return false;
            default:
                throw new IOException("Команда не распознана"); //TODO сделать отдельный класс для своих исключений
        }
    }

    //получение списка файлов относительно директории dir
    public String[] getLS(String dir) {
        String msg = "/ls " + dir;
        try {
            dataOutputStream.writeUTF(msg);
            msg = dataInputStream.readUTF();

            return msg.split("\\|");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void closeConnection() {

    }
}
