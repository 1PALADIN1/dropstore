import java.io.*;
import java.net.Socket;

public class SessionManager {
    //класс для управления клиентской сессией
    private Socket session;
    private String serverIp;
    private int serverPort;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String currentFolderId = "root";
    private String parentFolderId = "root";

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
    public void sendFileToServer(String fileName, String folderId, File file) {
        String msg = Command.UPLOAD.getCommandString() + " " + fileName + " " + folderId;
        FileInputStream fileInputStream = null;
        try {
            dataOutputStream.writeUTF(msg);
            msg = dataInputStream.readUTF();
            if (msg.equals(Command.CONTINUE.getCommandString())) {
                //File file = new File("download\\test.txt");
                //BufferedInputStream bis = new BufferedInputStream(new FileInputStream("download\\test.txt"));
                fileInputStream = new FileInputStream(file);
                byte[] fileBytes = new byte[fileInputStream.available()];
                fileInputStream.read(fileBytes);
                dataOutputStream.write(fileBytes);

                String[] data = dataInputStream.readUTF().split("\\|");
                switch (Command.getCommand(data[0])) {
                    case ERROR: throw new IOException(data[1]);
                    case OK: {
                        System.out.println(data[1]);
                    }
                    break;
                }
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

    public void createDirectory(String dirName, String folderId) throws IOException {
        String msg = Command.CREATEDIR.getCommandString() + " " + dirName + " " + folderId;
        dataOutputStream.writeUTF(msg);
        String[] data = dataInputStream.readUTF().split("\\s");

        //TODO добавить кастомные ошибки
        switch (Command.getCommand(data[0])) {
            case OK:
                System.out.println("Папка успешно создана");
                break;
            case ERROR:
                System.out.println("Ошибка! " + data[1]);
                break;
            default:
                System.out.println("Команда не распознана");
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

    //управление папками
    public void setCurrentFolderId(String currentFolderId) {
        if (currentFolderId.equals("null")) currentFolderId = "root";
        this.currentFolderId = currentFolderId;
    }

    public void setParentFolderId(String parentFolderId) {
        if (parentFolderId.equals("null")) parentFolderId = "root";
        this.parentFolderId = parentFolderId;
    }

    public String getCurrentFolderId() {
        return currentFolderId;
    }

    public String getParentFolderId() {
        return parentFolderId;
    }

    public String getServerParentFolderId(String folderId) throws IOException {
        String msg = Command.PARENTDIR.getCommandString() + " " + folderId;
        dataOutputStream.writeUTF(msg);

        String[] data = dataInputStream.readUTF().split("\\|");
        switch (Command.getCommand(data[0])) {
            case OK:
                System.out.println(data[1]);
                return data[1];
            case ERROR:
                System.out.println("Ошибка получения родительской папки " + data[1]);
                return "root";
            default:
                System.out.println("Команда не распознана");
        }
        return "root";
    }

    public void closeConnection() {

    }
}
