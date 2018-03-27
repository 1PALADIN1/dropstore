import dbmanager.AuthService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private AuthService authService;

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

            if (data.length == 2) {
                if (authService.login(data[0], data[1])) dataOutputStream.writeUTF("Поздравляем, Вы залогинились!");
                else
                    dataOutputStream.writeUTF("Неправильный логин и/или пароль!");
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
