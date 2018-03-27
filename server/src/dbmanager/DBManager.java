package dbmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class DBManager {
    //класс для управления подключениями к БД
    private Connection connection;

    DBManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:main.db");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (!connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void query() {

    }

    //проверка на существование записей
    public boolean checkExistence () {

        return false;
    }

    public void insert() {

    }

    public void update() {

    }

    public void delete() {

    }
}
