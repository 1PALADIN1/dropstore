import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        connect();
    }

    private void connect() {
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            String s = dataInputStream.readUTF();
            System.out.println(s);
            switch (s) {
                case "loginpassword":
                    dataOutputStream.writeUTF("Поздравляем! Вы залогинились!");
                break;
                default:
                    dataOutputStream.writeUTF("Бла-бла!");
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
