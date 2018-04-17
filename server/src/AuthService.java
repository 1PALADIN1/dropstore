import dbmanager.DBManager;

public class AuthService {
    //класс для работы с клиентскими сессиями
    private DBManager dbManager;

    public AuthService () {
        dbManager = new DBManager();
    }

    public synchronized boolean login(String login, String password) {
        return dbManager.checkExistence("users", "login = ? and password = ?", login, password);
    }

    public void close() {
        dbManager.disconnect();
    }

    public DBManager getDBConnection() {
        return dbManager;
    }
}
