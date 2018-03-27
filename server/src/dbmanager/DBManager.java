package dbmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class DBManager {
    //класс для управления подключениями к БД
    private Connection connection;
    private PreparedStatement preparedStatement;

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
            if (preparedStatement != null) preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void query() {

    }

    //проверка на существование записей
    public boolean checkExistence (String preparedQuery, String ... params) {
        try {
            preparedStatement = connection.prepareStatement(preparedQuery);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setString(i+1, params[i]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void insert() {

    }

    public void update() {

    }

    public void delete() {

    }
}
