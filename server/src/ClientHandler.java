import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler implements Runnable {
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private AuthService authService;
    private Command command;
    private boolean isAuth; //авторизован ли пользователь
    private String login;
    private SessionManager sessionManager;

    public ClientHandler(Socket socket, AuthService authService) {
        this.socket = socket;
        this.authService = authService;
        this.isAuth = false;
    }

    private void connect() {
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            sessionManager = new SessionManager(authService); //TODO сделать один на весь проект (либо сделать Singleton для DBManager)

            while (true) {
                String msg = dataInputStream.readUTF();
                System.out.println(msg);
                String[] data = msg.split("\\s");
                command = Command.getCommand(data[0]);

                if (isAuth) {

                    //операции для авторизованного клиента
                    if (data.length >= 2) {
                        switch (command) {
                            case LS: {
                                dataOutputStream.writeUTF(sessionManager.getFileList(login, data[1]));
                            }
                            break;
                            case UPLOAD: {
                                dataOutputStream.writeUTF(Command.CONTINUE.getCommandString());
                                if (dataInputStream.read() != -1) {
                                    byte[] fileBytes = new byte[dataInputStream.available()];
                                    dataInputStream.read(fileBytes);
                                    try {
                                        sessionManager.uploadFileOnServer(login, data[1], data[2], fileBytes);
                                        dataOutputStream.writeUTF(Command.OK.getCommandString() + "|Файл успешно загружен");
                                    } catch (IOException | CustomServerException | SQLException e) {
                                        dataOutputStream.writeUTF(Command.ERROR.getCommandString() + "|" + e.getMessage());
                                    }
                                }
                            }
                            break;
                            case DOWNLOAD: {
                                byte[] fileBytes;
                                try {
                                    fileBytes = sessionManager.downloadFileFromServer(login, data[1], data[2]);
                                    dataOutputStream.writeUTF(Command.CONTINUE.getCommandString());
                                    dataOutputStream.write(fileBytes);
                                } catch (Exception e) {
                                    dataOutputStream.writeUTF(Command.ERROR.getCommandString() + " " + e.getMessage());
                                    System.out.println("Ошибка при отправке файла: " + e.getMessage());
                                }
                            }
                            break;
                            case CREATEDIR: {
                                try {
                                    sessionManager.createDirectory(login, data[1], data[2]);
                                    dataOutputStream.writeUTF(Command.OK.getCommandString());
                                } catch (Exception e) {
                                    dataOutputStream.writeUTF(Command.ERROR.getCommandString() + " " + e.getMessage());
                                    System.out.println("Ошибка при создании директории: " + e.getMessage());
                                }
                            }
                            break;
                            case PARENTDIR: {
                                try {
                                    String parentFolderId = sessionManager.getParentFolderId(login, data[1]);
                                    dataOutputStream.writeUTF(Command.OK.getCommandString() + "|" + parentFolderId);
                                } catch (SQLException e) {
                                    dataOutputStream.writeUTF(Command.ERROR.getCommandString() + "|" + e.getMessage());
                                }
                            }
                            break;
                            case DELETE: {
                                try {
                                    sessionManager.deleteFileFromServer(login, data[1], data[2], data[3]);
                                    dataOutputStream.writeUTF(Command.OK.getCommandString() + "|Данные успешно удалены");
                                } catch (SQLException | CustomServerException e) {
                                    dataOutputStream.writeUTF(Command.ERROR.getCommandString() + "|" + e.getMessage());
                                    System.out.println("Ошибка удаления: " + e.getMessage());
                                }
                            }
                            break;
                            default:
                                dataOutputStream.writeUTF("Команда не распознана, обратитесь к администратору.");
                        }
                    }
                } else {
                    //если клиент не авторизован
                    if (data.length == 3) {
                        if (command == Command.AUTH) {
                            if (authService.login(data[1], data[2])) {
                                dataOutputStream.writeUTF(Command.OK.getCommandString());
                                login = data[1];
                                System.out.println("Пользователь " + login + " авторизовался в системе");
                                isAuth = true;
                            } else {
                                dataOutputStream.writeUTF(Command.ERROR.getCommandString());
                            }
                        } else {
                            if (command == Command.REG) {
                                if (authService.regUser(data[1], data[2])) {
                                    dataOutputStream.writeUTF(Command.OK.getCommandString());
                                    login = data[1];
                                    System.out.println("Пользователь " + login + " зарегистрировался в системе");
                                    isAuth = true;
                                } else {
                                    dataOutputStream.writeUTF(Command.ERROR.getCommandString());
                                }
                            } else {
                                dataOutputStream.writeUTF("Команда не распознана, обратитесь к администратору.");
                            }
                        }
                    } else {
                        dataOutputStream.writeUTF("Команда не распознана, обратитесь к администратору.");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void run() {
        connect();
    }
}
