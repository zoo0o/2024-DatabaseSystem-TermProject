import service.AuthService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String databaseURL = "jdbc:mysql://192.168.56.101:3308/clubdb";
        String username = "kimjiyu";
        String password = "1234";

        Scanner scanner = new Scanner(System.in);

        System.out.print("Do you want to initialize database? (y/n): ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("y")) {
            System.out.println("Initializing database...");
            DatabaseSetup.initialize();
            System.out.println("Database initialization complete.");
        } else {
            System.out.println("Skipping database initialization.");
        }

        try (Connection connection = DriverManager.getConnection(databaseURL, username, password);
             Statement statement = connection.createStatement()) {

            System.out.println("Connected to database 'clubdb'.");

            while (true) {
                System.out.println("\n=== Main Options ===");
                System.out.println("1. Sign Up");
                System.out.println("2. Sign In");
                System.out.println("3. Exit");
                System.out.print("Select option: ");
                int option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        AuthService.signUp(connection);
                        break;
                    case 2:
                        AuthService.signIn(connection);
                        break;
                    case 3:
                        System.out.println("Exiting program!");
                        return;
                    default:
                        System.out.println("Invalid selection. Please try again.");
                }
            }

        } catch (Exception e) {
            System.out.println("Error occurred while connecting to 'clubdb'.");
            e.printStackTrace();
        }
    }
}
