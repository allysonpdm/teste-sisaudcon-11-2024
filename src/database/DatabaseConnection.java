package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import config.Database;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://" + Database.DB_HOST + ":" + Database.DB_PORT + "/" + Database.DB_NAME;
        return DriverManager.getConnection(url, Database.DB_USER, Database.DB_PASSWORD);
    }
}
