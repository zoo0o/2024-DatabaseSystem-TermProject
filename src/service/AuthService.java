package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class AuthService {

    public static void signUp(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=== Sign Up ===");
        System.out.println("1. Student");
        System.out.println("2. Professor");
        System.out.println("3. Assistant");
        System.out.println("4. Back to Main Options");
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
            case 4:
                return;
            default:
                System.out.println("Invalid user type.");
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
            System.out.println("Passwords do not match. Try again.");
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
            System.out.print("Select status: ");
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
                    System.out.println("Invalid status.");
                    return;
            }
        }

        try {
            String sql = isStudent
                    ? "INSERT INTO " + tableName + " (" + idColumn + ", pwd, name, department, status, phone) VALUES (?, ?, ?, ?, ?, ?)"
                    : "INSERT INTO " + tableName + " (" + idColumn + ", pwd, name, department, phone) VALUES (?, ?, ?, ?, ?)";

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
                    System.out.println("Signed up successfully!");
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred during sign up.");
            e.printStackTrace();
        }
    }

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

        boolean isPresident = false;
        int clubId = 0;
        String clubName = "";

        try {
            String checkPresidentSql = "SELECT cid, name FROM club WHERE president_sid = ?";
            try (PreparedStatement statement = connection.prepareStatement(checkPresidentSql)) {
                statement.setInt(1, userId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        isPresident = true;
                        clubId = resultSet.getInt("cid");
                        clubName = resultSet.getString("name");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while checking president status.");
            e.printStackTrace();
        }

        while (true) {
            System.out.println("\n=== Student Options ===");
            System.out.println("1. View Club List");
            System.out.println("2. View My Club List");
            System.out.println("3. Join Club");
            System.out.println("4. Leave Club");
            System.out.println("5. Sign Out");
            if (isPresident) {
                System.out.printf(">> Club [%s] Manage\n", clubName);
                System.out.println("6. Edit Club Information");
                System.out.println("7. View Members");
                System.out.println("8. Manage Documents");
            }
            System.out.print("Select option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    CommonService.viewClubList(connection);
                    break;
                case 2:
                    StudentService.viewMyClubs(userId, connection);
                    break;
                case 3:
                    StudentService.joinClub(userId, connection);
                    break;
                case 4:
                    StudentService.leaveClub(userId, connection);
                    break;
                case 5:
                    return;
                case 6:
                    if (isPresident) {
                        PresidentService.editClubInformation(clubId, connection);
                    } else {
                        System.out.println("Invalid selection.");
                    }
                    break;
//                case 7:
//                    if (isPresident) {
//                        PresidentService.viewClubMembers(clubId, connection);
//                    } else {
//                        System.out.println("Invalid selection.");
//                    }
//                    break;
//                case 8:
//                    if (isPresident) {
//                        PresidentService.manageDocuments(clubId, connection);
//                    } else {
//                        System.out.println("Invalid selection.");
//                    }
//                    break;
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
                    CommonService.viewClubList(connection);
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
            System.out.println("3. View Club Members");
            System.out.println("4. View Student List");
            System.out.println("5. View Professor List");
            System.out.println("6. View Assistant List");
            System.out.println("7. Sign Out");
            System.out.print("Select option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    AssistantService.createClub(connection);
                    break;
                case 2:
                    CommonService.viewClubList(connection);
                    break;
                case 3:
                    AssistantService.viewClubMembers(connection);
                    break;
                case 4:
                    AssistantService.viewStudentList(connection);
                    break;
                case 5:
                    AssistantService.viewProfessorList(connection);
                    break;
                case 6:
                    AssistantService.viewAssistantList(connection);
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid selection.");
            }
        }
    }
}
