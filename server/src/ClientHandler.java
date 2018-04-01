import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private AuthService authService;
    private Command command;
    private boolean isAuth; //авторизован ли пользователь
    private String login;

    public ClientHandler(Socket socket, AuthService authService) {
        this.socket = socket;
        this.authService = authService;
        this.isAuth = false;
    }

    private void connect() {
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            while (true) {
                String msg = dataInputStream.readUTF();
                System.out.println(msg);
                String[] data = msg.split("\\s");
                command = Command.getCommand(data[0]);

                if (isAuth) {
                    dataOutputStream.writeUTF("Вы в сети");
                    //операции для авторизованного клиента
                    if (data.length >= 2) {

                    }
                } else {
                    //если клиент не авторизован
                    if (data.length == 3 && command == Command.AUTH) {
                        if (authService.login(data[1], data[2])) {
                            dataOutputStream.writeUTF("Поздравляем, Вы залогинились!");
                            login = data[1];
                            System.out.println("Пользователь " + login + " авторизовался в системе");
                            isAuth = true;

                        } else {
                            dataOutputStream.writeUTF("Неправильный логин и/или пароль!");
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
