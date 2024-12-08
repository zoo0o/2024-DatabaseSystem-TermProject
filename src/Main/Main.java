package Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {
        String jdbcURL = "jdbc:mysql://192.168.56.101:3308";
        String username = "kimjiyu";
        String password = "1234";
        String databaseName = "clubdb";

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password); Statement statement = connection.createStatement()) {

            String checkDatabaseSQL = "SHOW DATABASES LIKE '" + databaseName + "'";
            try (ResultSet resultSet = statement.executeQuery(checkDatabaseSQL)) {
                if (resultSet.next()) {
                    System.out.println("Database '" + databaseName + "' already exists.");
                } else {
                    System.out.println("Database '" + databaseName + "' does not exist. Initializing...");
                    DatabaseSetup.initialize();
                    System.out.println("Database initialized successfully!");
                }
            }

            String useDatabaseSQL = "USE " + databaseName;
            statement.execute(useDatabaseSQL);

            SignUp.signUp(connection);
        } catch (Exception e) {
            System.out.println("Error occurred.");
            e.printStackTrace();
        }
    }
}
