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
        System.out.println("4. Back to Main Options");
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
            case 4:
                return;
            default:
                System.out.println("Invalid selection.");
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
                        grantAccess(userType, id, connection);
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

    private static void grantAccess(int userType, int userId, Connection connection) {
        switch (userType) {
            case 1:
                System.out.println("Access granted: Student.");
                studentOptions(userId, connection);
                break;
            case 2:
                System.out.println("Access granted: Professor.");
                professorOptions(userId, connection);
                break;
            case 3:
                System.out.println("Access granted: Assistant.");
                assistantOptions(userId, connection);
                break;
            default:
                System.out.println("Unknown user type.");
        }
    }

    private static void studentOptions(int userId, Connection connection) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Student Options ===");
            System.out.println("1. View Club List");
            System.out.println("2. Sign Out");
            System.out.print("Select option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    ViewClubs.viewClubList(connection);
                    break;
                case 2:
                    return;
                default:
                    System.out.println("Invalid selection.");
            }
        }
    }

    private static void professorOptions(int userId, Connection connection) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Professor Options ===");
            System.out.println("1. View Club List");
            System.out.println("2. Sign Out");
            System.out.print("Select option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    ViewClubs.viewClubList(connection);
                    break;
                case 2:
                    return;
                default:
                    System.out.println("Invalid selection.");
            }
        }
    }
    private static void assistantOptions(int userId, Connection connection) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Assistant Options ===");
            System.out.println("1. Create Club");
            System.out.println("2. View Club List");
            System.out.println("3. View Student List");
            System.out.println("4. View Professor List");
            System.out.println("5. View Assistant List");
            System.out.println("6. Sign Out");
            System.out.print("Select option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    CreateClub.createClub(connection);
                    break;
                case 2:
                    ViewClubs.viewClubList(connection);
                    break;
                case 3:
                    ViewStudents.viewStudentList(connection);
                    break;
                case 4:
                    ViewProfessors.viewProfessorList(connection);
                    break;
                case 5:
                    ViewAssistants.viewAssistantList(connection);
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid selection.");
            }
        }
    }
}
