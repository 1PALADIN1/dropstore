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
    public boolean checkExistence (String tableName, String whereExpression, String ... params) {
        try {
            String preparedQuery = "SELECT * FROM " + tableName + " WHERE " + whereExpression;
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
        if (logTableName == null || logTableName.isEmpty()) System.out.println("Таблица для логирования не задана");
    }


    //вставка новой записи в таблицу
    public void insert(String tableName, String[] columns, String[] params) {
        if (columns.length > 0 && params.length > 0 && columns.length == params.length) { //TODO добвить exception
            StringBuilder columnString = new StringBuilder(columns[0]);
            StringBuilder paramString = new StringBuilder("?");
            for (int i = 1; i < columns.length; i++) {
                columnString.append(", ").append(columns[i]);
                paramString.append(", ").append("?");
            }
            StringBuilder preparedQuery = new StringBuilder("INSERT INTO " + tableName + " (" + columnString + ")"
                                                            + " VALUES (" + paramString + ")");

            try {
                preparedStatement = connection.prepareStatement(preparedQuery.toString());
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setString(i+1, params[i]);
                }
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {

    }

    public void delete() {

    }
}
