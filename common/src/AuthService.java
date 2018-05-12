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

    //регистрация нового пользователя
    public boolean regUser(String login, String password) {
        if (dbManager.checkExistence("users", "login = ?", login)) return false;
        else {
            dbManager.insert("users", new String[] { "login", "password" }, new String[] { login, password });
            return true;
        }
    }

    public void close() {
        dbManager.disconnect();
    }

    public DBManager getDBConnection() {
        return dbManager;
    }
}
