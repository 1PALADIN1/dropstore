package dbmanager;

import java.sql.*;

public class DBManager {
    //класс для управления подключениями к БД
    private Connection connection;
    private PreparedStatement preparedStatement;
    private String logTableName; //название таблицы для логирования

    public DBManager() {
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
            if (preparedStatement.executeQuery().next()) return true;
            //if (rs.first()) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setLogTableName(String logTableName) {
        this.logTableName = logTableName;
    }

    public void log(String logMessage) {
        //логирование данных в таблицу
    }


    public void insert(String tableName, String ... params) {

    }

    public void update() {

    }

    public void delete() {

    }
}
