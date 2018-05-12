import java.io.*;
import java.net.Socket;

public class ClientSession {
    //класс для управления клиентской сессией
    private Socket session;
    private static ClientSession clientSession;
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 5654;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String currentFolderId = "root";
    private String parentFolderId = "root";

    private ClientSession() throws IOException {
        //this.serverIp = serverIp;
        //this.serverPort = serverPort;
        openConnection();
    }

    public static ClientSession getClientSession() throws IOException {
        if (clientSession == null) {
            clientSession = new ClientSession();
        }
        return clientSession;
    }

    private void openConnection() throws IOException {
        if (session == null || session.isClosed()) {
            session = new Socket(SERVER_IP, SERVER_PORT);
            dataInputStream = new DataInputStream(session.getInputStream());
            dataOutputStream = new DataOutputStream(session.getOutputStream());
        }
    }

    public boolean authUser(String login, String password) throws CustomClientException, IOException {
        if (login.isEmpty() || password.isEmpty()) throw new CustomClientException("Логин/пароль не должен быть пустым");
        String msg = Command.AUTH.getCommandString() + "|" + login + "|" + password; //временно для отладки
        dataOutputStream.writeUTF(msg);
        String[] data = dataInputStream.readUTF().split("\\|");

        //запрос на авторизацию
        switch (Command.getCommand(data[0])) {
            //временные заглушки
            case OK: return true;
            case ERROR: throw new CustomClientException(data[1]);
            default:
                throw new CustomClientException("Команда не распознана");
        }
    }

    public boolean regUser(String login, String password) throws IOException, CustomClientException { //TODO поправить метод
        String msg = Command.REG.getCommandString() + "|" + login + "|" + password;
        dataOutputStream.writeUTF(msg);
        String[] data = dataInputStream.readUTF().split("\\|");

        switch (Command.getCommand(data[0])) {
            case OK: return true;
            case ERROR: return false;
            default:
                throw new CustomClientException("Команда не распознана");
        }
    }

    //получение списка файлов относительно директории dir
    public String[] getLS(String dir) throws IOException {
        String msg = Command.LS.getCommandString() + "|" + dir;
            dataOutputStream.writeUTF(msg);
            msg = dataInputStream.readUTF();
            return msg.split("\\|");
    }

    //отправка файла на сервер
    public void sendFileToServer(String fileName, String folderId, File file) throws IOException, CustomClientException {
        String msg = Command.UPLOAD.getCommandString() + "|" + fileName + "|" + folderId;
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
                    case ERROR: throw new CustomClientException(data[1]);
                    case OK: {
                        System.out.println(data[1]);
                    }
                    break;
                }
            }
        } finally {
            if (fileInputStream != null) fileInputStream.close();
        }
    }

    public void createDirectory(String dirName, String folderId) throws IOException, CustomClientException {
        String msg = Command.CREATEDIR.getCommandString() + "|" + dirName + "|" + folderId;
        dataOutputStream.writeUTF(msg);
        String[] data = dataInputStream.readUTF().split("\\|");

        switch (Command.getCommand(data[0])) {
            case OK:
                System.out.println("Папка успешно создана");
                break;
            case ERROR: throw new CustomClientException(data[1]);
            default:
                throw new CustomClientException("Команда не распознана");
        }
    }

    //скачивание файла с сервера
    public void downloadFileFromServer(String fileName, String folderId) throws Exception {

        File rootFolder = new File("//download"); //папка для загрузки
        if (!rootFolder.exists()) rootFolder.mkdirs();

        String msg = Command.DOWNLOAD.getCommandString() + "|" + fileName + "|" + folderId;
        dataOutputStream.writeUTF(msg);
        String[] data = dataInputStream.readUTF().split("\\|");

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
    public void deleteFileFromServer(String fileName, String folderId, String objectType) throws IOException, CustomClientException {
        String msg = Command.DELETE.getCommandString() + "|" + fileName + "|" + folderId + "|" + objectType;
        dataOutputStream.writeUTF(msg);
        String[] data = dataInputStream.readUTF().split("\\|");

        switch (Command.getCommand(data[0])) {
            case OK:
                System.out.println(data[1]);
                break;
            case ERROR: throw new CustomClientException(data[1]);
            default:
                throw new CustomClientException("Команда не распознана");
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

    public String getServerParentFolderId(String folderId) throws IOException, CustomClientException {
        String msg = Command.PARENTDIR.getCommandString() + "|" + folderId;
        dataOutputStream.writeUTF(msg);

        String[] data = dataInputStream.readUTF().split("\\|");
        switch (Command.getCommand(data[0])) {
            case OK:
                System.out.println(data[1]);
                return data[1];
            case ERROR: throw new CustomClientException(data[1]);
            default:
                throw new CustomClientException("Команда не распознана");
        }
    }

    public void closeConnection() {

    }
}
