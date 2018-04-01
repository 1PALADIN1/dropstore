import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private Socket socket;
    private ServerSocket serverSocket;
    private final static int SERVER_PORT = 5654;
    private AuthService authService;

    public Server() {
        System.out.println("Запуск сервера...");

        authService = new AuthService();

        //открываем подключение к хранилищу
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Сервер запущен");

            while (true) {
                socket = serverSocket.accept();
                new ClientHandler(socket, authService); //создаём новую клиентскую сессию
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
