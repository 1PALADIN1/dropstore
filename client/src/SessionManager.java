import java.io.*;
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
            case "/ok": return true;
            case "/error": return false;
            default:
                throw new IOException("Команда не распознана"); //TODO сделать отдельный класс для своих исключений
        }
    }

    public boolean regUser(String login, String password) throws IOException {
        String msg = Command.REG.getCommandString() + " " + login + " " + password;
        dataOutputStream.writeUTF(msg);
        msg = dataInputStream.readUTF();

        switch (msg) {
            //временные заглушки
            case "/ok": return true;
            case "/error": return false;
            default:
                throw new IOException("Команда не распознана");
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

    //отправка файла на сервер
    public void sendFileToServer(String fileName) {
        String msg = Command.UPLOAD.getCommandString() + " " + fileName + " 3";
        FileInputStream fileInputStream = null;
        try {
            dataOutputStream.writeUTF(msg);
            msg = dataInputStream.readUTF();
            if (msg.equals(Command.CONTINUE.getCommandString())) {
                //File file = new File("download\\test.txt");
                //BufferedInputStream bis = new BufferedInputStream(new FileInputStream("download\\test.txt"));
                fileInputStream = new FileInputStream("download\\test111.mdm");
                byte[] fileBytes = new byte[fileInputStream.available()];
                fileInputStream.read(fileBytes);
                dataOutputStream.write(fileBytes);
            } //TODO добавить обработку ошибки от сервера
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //скачивание файла с сервера
    public void downloadFileFromServer(String fileName, String folderId) throws Exception {

        File rootFolder = new File("//download"); //папка для загрузки
        if (!rootFolder.exists()) rootFolder.mkdirs();

        String msg = Command.DOWNLOAD.getCommandString() + " " + fileName + " " + folderId;
        dataOutputStream.writeUTF(msg);
        String[] data = dataInputStream.readUTF().split("\\s");

        if (data[0].equals(Command.ERROR.getCommandString())) throw new Exception(data[1]);
        if (data[0].equals(Command.CONTINUE.getCommandString())) {
            if (dataInputStream.read() != -1) {
                byte[] fileBytes = new byte[dataInputStream.available()];
                dataInputStream.read(fileBytes);

                File file = new File("download//" + fileName);
                file.createNewFile();
                FileOutputStream fileOut = new FileOutputStream("download//" + fileName);
                fileOut.write(fileBytes);
            }
        }
    }

    //удаление файла
    public void deleteFileFromServer(String filePath, String fileName) {
        String msg = "/del " + filePath + " " + fileName;
        try {
            dataOutputStream.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {

    }
}
