import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private AuthService authService;
    private Command command;
    private boolean isAuth; //авторизован ли пользователь

    public ClientHandler(Socket socket, AuthService authService) {
        this.socket = socket;
        this.authService = authService;
        this.isAuth = false;
        connect();
    }

    private void connect() {
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            String msg = dataInputStream.readUTF();
            System.out.println(msg);
            String[] data = msg.split("\\s");
            command = Command.getCommand(data[0]);

            if (isAuth) {
                //операции для авторизованного клиента
                if (data.length >= 2) {

                }
            } else {
                //если клиент не авторизован
                if (data.length == 3 && command == Command.AUTH) {
                    if (authService.login(data[1], data[2])) {
                        dataOutputStream.writeUTF("Поздравляем, Вы залогинились!");
                        System.out.println("Пользователь " + data[1] + " авторизовался в системе");
                        isAuth = true;
                    }
                    else {
                        dataOutputStream.writeUTF("Неправильный логин и/или пароль!");
                    }
                } else {
                    dataOutputStream.writeUTF("Команда не распознана, обратитесь к администратору.");
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
}
