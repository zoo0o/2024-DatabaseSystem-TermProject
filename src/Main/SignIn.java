package Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class SignIn {

    public static void signIn(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=== Sign In ===");
        System.out.println("1. Student");
        System.out.println("2. Professor");
        System.out.println("3. Assistant");
        System.out.print("Select user type: ");

        int userType = scanner.nextInt();
        scanner.nextLine();

        String tableName = null;
        String idColumn = null;

        switch (userType) {
            case 1:
                tableName = "student";
                idColumn = "sid";
                break;
            case 2:
                tableName = "professor";
                idColumn = "pid";
                break;
            case 3:
                tableName = "assistant";
                idColumn = "aid";
                break;
            default:
                System.out.println("Invalid user type choice.");
                return;
        }

        System.out.print("Enter ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        try {
            String sql = "SELECT name FROM " + tableName + " WHERE " + idColumn + " = ? AND pwd = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                statement.setString(2, password);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("Login successful!");
                        grantAccess(userType);
                    } else {
                        System.out.println("Invalid ID or Password.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred during sign in.");
            e.printStackTrace();
        }
    }

    private static void grantAccess(int userType) {
        switch (userType) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                System.out.println("Unknown user type.");
        }
    }
}
