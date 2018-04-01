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

    public ClientHandler(Socket socket, AuthService authService) {
        this.socket = socket;
        this.authService = authService;
        connect();
    }

    private void connect() {
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            String msg = dataInputStream.readUTF();
            System.out.println(msg);
            String[] data = msg.split("\\s");

            if (data.length >= 2) {
                command = Command.getCommand(data[0]);
                switch (command) {
                    case AUTH: {
                        if (data.length == 3) {
                            if (authService.login(data[1], data[2])) dataOutputStream.writeUTF("Поздравляем, Вы залогинились!");
                            else
                                dataOutputStream.writeUTF("Неправильный логин и/или пароль!");
                        }
                        else
                            dataOutputStream.writeUTF("Неверное количество параметров на вход");
                    }
                    break;
                    default:
                        dataOutputStream.writeUTF("Команда не распознана, обратитесь к администратору.");
                }
            } else {
                dataOutputStream.writeUTF("Неправильный логин и/или пароль!");
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
