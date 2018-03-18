import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static Socket socket;
    private static ServerSocket serverSocket;
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;
    private final static int SERVER_PORT = 5654;

    public static void main(String[] args) {
        System.out.println("Запуск сервера...");

        //открываем подключение к хранилищу
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Сервер запущен");

            while (true) {
                socket = serverSocket.accept();
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                System.out.println(dataInputStream.readUTF());
                dataOutputStream.writeUTF("Бла-бла!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null && !socket.isClosed()) socket.close();
                if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
