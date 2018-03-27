package dbmanager;

public class AuthService {
    //класс для работы с клиентскими сессиями
    private DBManager dbManager;

    public AuthService () {
        dbManager = new DBManager();
    }

    public boolean login(String login, String password) {
        return dbManager.checkExistence("SELECT * FROM users WHERE login = ? and password = ?", login, password);
    }

    public void close() {
        dbManager.disconnect();
    }
}
