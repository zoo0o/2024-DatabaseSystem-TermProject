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
            System.out.println("1. 재학   2. 휴학   3. 졸업");
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
            System.out.println("5. View My Profile");
            System.out.println("6. Update My Profile");
            System.out.println("7. Sign Out");
            if (isPresident) {
                System.out.printf(">> Club %s Manage\n", clubName);
                System.out.println("   8. Edit Club Information");
                System.out.println("   9. View Members");
                System.out.println("   10. Manage Documents");
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
                    viewProfile(connection, 1, userId);
                    break;
                case 6:
                    updateProfile(connection, 1, userId);
                    break;
                case 7:
                    return;
                case 8:
                    if (isPresident) {
                        PresidentService.editClubInformation(clubId, connection);
                    } else {
                        System.out.println("Invalid selection.");
                    }
                    break;
                case 9:
                    if (isPresident) {
                        PresidentService.viewMyClubMembers(clubId, connection);
                    } else {
                        System.out.println("Invalid selection.");
                    }
                    break;
                case 10:
                    if (isPresident) {
                        PresidentService.manageDocuments(clubId, connection);
                    } else {
                        System.out.println("Invalid selection.");
                    }
                    break;
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
            System.out.println("2. View My Club");
            System.out.println("3. View My Profile");
            System.out.println("4. Update My Profile");
            System.out.println("5. View Student List");
            System.out.println("6. View Professor List");
            System.out.println("7. View Assistant List");
            System.out.println("8. Sign Out");
            System.out.print("Select option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    CommonService.viewClubList(connection);
                    break;
                case 2:
                    viewProfessorClubs(userId, connection);
                    break;
                case 3:
                    viewProfile(connection, 2, userId);
                    break;
                case 4:
                    updateProfile(connection, 2, userId);
                    break;
                case 5:
                    AssistantService.viewStudentList(connection);
                    break;
                case 6:
                    AssistantService.viewProfessorList(connection);
                    break;
                case 7:
                    AssistantService.viewAssistantList(connection);
                    break;
                case 8:
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
            System.out.println("2. Delete Club");
            System.out.println("3. Manage Documents");
            System.out.println("4. View Club List");
            System.out.println("5. View Club Members List");
            System.out.println("6. View Student List");
            System.out.println("7. View Professor List");
            System.out.println("8. View Assistant List");
            System.out.println("9. View My Profile");
            System.out.println("10. Update My Profile");
            System.out.println("11. Sign Out");
            System.out.print("Select option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    AssistantService.createClub(connection);
                    break;
                case 2:
                    AssistantService.deleteClub(connection);
                    break;
                case 3:
                    AssistantService.manageDocuments(userId, connection);
                    break;
                case 4:
                    CommonService.viewClubList(connection);
                    break;
                case 5:
                    AssistantService.viewClubMembers(connection);
                    break;
                case 6:
                    AssistantService.viewStudentList(connection);
                    break;
                case 7:
                    AssistantService.viewProfessorList(connection);
                    break;
                case 8:
                    AssistantService.viewAssistantList(connection);
                    break;
                case 9:
                    viewProfile(connection, 3, userId);
                    break;
                case 10:
                    updateProfile(connection, 3, userId);
                    break;
                case 11:
                    return;
                default:
                    System.out.println("Invalid selection.");
            }
        }
    }


    public static void viewProfile(Connection connection, int userType, int userId) {
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
                System.out.println("Invalid user type.");
                return;
        }

        try {
            String sql = "SELECT * FROM " + tableName + " WHERE " + idColumn + " = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("\n=== My Profile ===");
                        System.out.println("ID: " + resultSet.getInt(idColumn));
                        System.out.println("Name: " + resultSet.getString("name"));
                        System.out.println("Department: " + resultSet.getString("department"));
                        System.out.println("Phone: " + resultSet.getString("phone"));
                        if (userType == 1) { // Student specific field
                            System.out.println("Status: " + resultSet.getString("status"));
                        }
                    } else {
                        System.out.println("Profile not found.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while viewing profile.");
            e.printStackTrace();
        }
    }


    public static void updateProfile(Connection connection, int userType, int userId) {
        Scanner scanner = new Scanner(System.in);
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
                System.out.println("Invalid user type.");
                return;
        }

        System.out.println("\n=== Update Profile ===");

        System.out.print("Enter Current Password: ");
        String currentPassword = scanner.nextLine();

        try {
            String checkPasswordSql = "SELECT pwd FROM " + tableName + " WHERE " + idColumn + " = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkPasswordSql)) {
                checkStatement.setInt(1, userId);

                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String storedPassword = resultSet.getString("pwd");
                        if (!storedPassword.equals(currentPassword)) {
                            System.out.println("Current password is incorrect.");
                            return;
                        }
                    } else {
                        System.out.println("User not found.");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while verifying password.");
            e.printStackTrace();
            return;
        }

        String newPassword = null;
        System.out.print("Enter New Password (Leave blank to skip): ");
        String passwordInput = scanner.nextLine();
        if (!passwordInput.isBlank()) {
            System.out.print("Confirm New Password: ");
            String confirmPassword = scanner.nextLine();
            while (!passwordInput.equals(confirmPassword)) {
                System.out.println("Passwords do not match. Try again.");
                System.out.print("Enter New Password: ");
                passwordInput = scanner.nextLine();
                System.out.print("Confirm New Password: ");
                confirmPassword = scanner.nextLine();
            }
            newPassword = passwordInput;
        }

        System.out.print("Enter New Department (Leave blank to skip): ");
        String department = scanner.nextLine();

        System.out.print("Enter New Phone Number (Leave blank to skip): ");
        String phone = scanner.nextLine();

        String status = null;
        if (userType == 1) {
            System.out.println("1. 재학   2. 휴학   3. 졸업");
            System.out.print("Select New Status (Leave blank to skip): ");
            String statusChoice = scanner.nextLine();
            switch (statusChoice) {
                case "1":
                    status = "재학";
                    break;
                case "2":
                    status = "휴학";
                    break;
                case "3":
                    status = "졸업";
                    break;
                case "":
                    break;
                default:
                    System.out.println("Invalid status.");
                    return;
            }
        }

        try {
            StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
            boolean hasUpdates = false;

            if (newPassword != null) {
                sql.append("pwd = ?, ");
                hasUpdates = true;
            }
            if (!department.isBlank()) {
                sql.append("department = ?, ");
                hasUpdates = true;
            }
            if (!phone.isBlank()) {
                sql.append("phone = ?, ");
                hasUpdates = true;
            }
            if (status != null) {
                sql.append("status = ?, ");
                hasUpdates = true;
            }

            if (!hasUpdates) {
                System.out.println("No changes were made.");
                return;
            }

            sql.setLength(sql.length() - 2);
            sql.append(" WHERE ").append(idColumn).append(" = ?");

            try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
                int paramIndex = 1;

                if (newPassword != null) {
                    statement.setString(paramIndex++, newPassword);
                }
                if (!department.isBlank()) {
                    statement.setString(paramIndex++, department);
                }
                if (!phone.isBlank()) {
                    statement.setString(paramIndex++, phone);
                }
                if (status != null) {
                    statement.setString(paramIndex++, status);
                }

                statement.setInt(paramIndex, userId);

                int rows = statement.executeUpdate();
                if (rows > 0) {
                    System.out.println("Profile updated successfully!");
                } else {
                    System.out.println("Failed to update profile.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while updating profile.");
            e.printStackTrace();
        }
    }

    private static void viewProfessorClubs(int professorId, Connection connection) {
        String query = """
        SELECT c.cid, c.name, c.is_academic, c.location, 
               s.name AS president_name
        FROM club c
        LEFT JOIN student s ON c.president_sid = s.sid
        WHERE c.advisor_pid = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, professorId);

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n=== My Club ===");
                System.out.println("ID | Name | Is Academic | Location | President");
                System.out.println("------------------------------------------------------------");

                if (rs.next()) {
                    int clubId = rs.getInt("cid");
                    String name = rs.getString("name");
                    boolean isAcademic = rs.getBoolean("is_academic");
                    String location = rs.getString("location");
                    String presidentName = rs.getString("president_name");

                    System.out.println(clubId + " | " + name + " | " + (isAcademic ? "Yes" : "No")
                            + " | " + location + " | " + (presidentName != null ? presidentName : "N/A"));
                } else {
                    System.out.println("You are not advising any clubs.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while retrieving my club.");
            e.printStackTrace();
        }
    }

}
