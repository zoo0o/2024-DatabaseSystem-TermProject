package Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class SignUp {
    public static void signUp(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=== Sign Up ===");
        System.out.println("1. Student");
        System.out.println("2. Professor");
        System.out.println("3. Assistant");
        System.out.print("Select user type: ");

        int userType = scanner.nextInt();
        scanner.nextLine();

        String tableName = null;
        String idColumn = null;
        boolean isStudent = false;

        switch (userType) {
            case 1:
                tableName = "student";
                idColumn = "sid";
                isStudent = true;
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

        System.out.print("Confirm Password: ");
        String confirmPassword = scanner.nextLine();

        while (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Please try again.");

            System.out.print("Enter Password: ");
            password = scanner.nextLine();

            System.out.print("Confirm Password: ");
            confirmPassword = scanner.nextLine();
        }

        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Department: ");
        String department = scanner.nextLine();

        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();

        String status = null;
        if (isStudent) {
            System.out.println("1. 재학");
            System.out.println("2. 휴학");
            System.out.println("3. 졸업");
            System.out.print("Select Status: ");
            int statusChoice = scanner.nextInt();
            scanner.nextLine();

            switch (statusChoice) {
                case 1:
                    status = "재학";
                    break;
                case 2:
                    status = "휴학";
                    break;
                case 3:
                    status = "졸업";
                    break;
                default:
                    System.out.println("Invalid status choice.");
                    return;
            }
        }

        try {
            String sql;
            if (isStudent) {
                sql = "INSERT INTO " + tableName + " (" + idColumn + ", pwd, name, department, status, phone) VALUES (?, ?, ?, ?, ?, ?)";
            } else {
                sql = "INSERT INTO " + tableName + " (" + idColumn + ", pwd, name, department, phone) VALUES (?, ?, ?, ?, ?)";
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                statement.setString(2, password);
                statement.setString(3, name);
                statement.setString(4, department);
                if (isStudent) {
                    statement.setString(5, status);
                    statement.setString(6, phone);
                } else {
                    statement.setString(5, phone);
                }

                int rows = statement.executeUpdate();
                if (rows > 0) {
                    System.out.println("signed up successfully!");
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred during sign up.");
            e.printStackTrace();
        }
    }
}
