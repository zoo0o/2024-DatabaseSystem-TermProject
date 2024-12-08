package Main;

import java.sql.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class DatabaseSetup {

    public static void initialize() {
        String jdbcURL = "jdbc:mysql://192.168.56.101:3308";
        String username = "kimjiyu";
        String password = "1234";
        String sqlFilePath = "src/resources/create.sql";

        try (
                Connection connection = DriverManager.getConnection(jdbcURL, username, password);
                Statement statement = connection.createStatement();
                BufferedReader reader = new BufferedReader(new FileReader(sqlFilePath))
        ) {
            String line;
            StringBuilder sqlBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                sqlBuilder.append(line).append("\n");
            }

            String[] sqlCommands = sqlBuilder.toString().split(";");
            for (String command : sqlCommands) {
                if (!command.trim().isEmpty()) {
                    statement.execute(command);
                }
            }

            System.out.println("SQL script executed successfully!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
