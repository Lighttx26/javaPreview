package ConnectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager() {
        // Initialize the database connection
        connect();
    }

    private void connect() {
        String url = "jdbc:mysql://localhost:3306/javapreview";
        String username = "root";
        String password = "admin";

        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Disconnected from the database.");
            } catch (SQLException e) {
                System.out.println("Failed to close the database connection.");
                e.printStackTrace();
            }
        }
    }
}