package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AssistantService {

    public static void createClub(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=== Create Club ===");
        System.out.print("Enter Club Name: ");
        String clubName = scanner.nextLine();

        System.out.print("Is this Academic Club? (y/n): ");
        String isAcademicInput = scanner.nextLine().toLowerCase();

        boolean isAcademic = isAcademicInput.equals("y");
        System.out.print("Enter Club Location: ");
        String location = scanner.nextLine();

        System.out.print("Enter Student ID for Club President: ");
        int presidentSid = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Professor ID for Club Advisor: ");
        int advisorPid = scanner.nextInt();
        scanner.nextLine();

        if (!isValidStudent(connection, presidentSid)) {
            System.out.println("Invalid student ID.");
            return;
        }

        if (isStudentAlreadyPresident(connection, presidentSid)) {
            System.out.println("Student is already a president of another club.");
            return;
        }

        if (!isValidProfessor(connection, advisorPid)) {
            System.out.println("Invalid professor ID.");
            return;
        }

        if (isProfessorAlreadyAdvisor(connection, advisorPid)) {
            System.out.println("Professor is already an advisor for another club.");
            return;
        }

        try {
            String insertClubSQL = "INSERT INTO club (name, is_academic, location, president_sid, advisor_pid) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement insertClubStmt = connection.prepareStatement(insertClubSQL)) {
                insertClubStmt.setString(1, clubName);
                insertClubStmt.setBoolean(2, isAcademic);
                insertClubStmt.setString(3, location);
                insertClubStmt.setInt(4, presidentSid);
                insertClubStmt.setInt(5, advisorPid);

                insertClubStmt.executeUpdate();
                System.out.println("Club created successfully!");
            }
        } catch (Exception e) {
            System.out.println("Error occurred while creating club.");
            e.printStackTrace();
        }
    }

    public static void viewClubMembers(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Club Name: ");
        String clubName = scanner.nextLine();

        String query = "SELECT s.sid, s.name, s.department, s.status, s.phone " +
                "FROM clubmember cm " +
                "JOIN student s ON cm.sid = s.sid " +
                "JOIN club c ON cm.cid = c.cid WHERE c.name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, clubName);

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n=== Members of " + clubName + " ===");
                System.out.println("ID | Name | Department | Status | Phone");
                while (rs.next()) {
                    int studentId = rs.getInt("sid");
                    String name = rs.getString("name");
                    String department = rs.getString("department");
                    String status = rs.getString("status");
                    String phone = rs.getString("phone");

                    System.out.println(studentId + " | " + name + " | " + department + " | " + status + " | " + phone);
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while fetching club members.");
            e.printStackTrace();
        }
    }

    public static void viewStudentList(Connection connection) {
        String query = "SELECT sid, name, department, status, phone FROM student";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n=== Student List ===");
            System.out.println("ID | Name | Department | Status | Phone");
            System.out.println("---------------------------------------");

            while (rs.next()) {
                int studentId = rs.getInt("sid");
                String name = rs.getString("name");
                String department = rs.getString("department");
                String status = rs.getString("status");
                String phone = rs.getString("phone");

                System.out.println(studentId + " | " + name + " | " + department + " | " + status + " | " + phone);
            }

        } catch (Exception e) {
            System.out.println("Error occurred while fetching student list.");
            e.printStackTrace();
        }
    }

    public static void viewProfessorList(Connection connection) {
        String query = "SELECT pid, name, department, phone FROM professor";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n=== Professor List ===");
            System.out.println("ID | Name | Department | Phone");
            System.out.println("--------------------------------");

            while (rs.next()) {
                int professorId = rs.getInt("pid");
                String name = rs.getString("name");
                String department = rs.getString("department");
                String phone = rs.getString("phone");

                System.out.println(professorId + " | " + name + " | " + department + " | " + phone);
            }

        } catch (Exception e) {
            System.out.println("Error occurred while fetching professor list.");
            e.printStackTrace();
        }
    }

    public static void viewAssistantList(Connection connection) {
        String query = "SELECT aid, name, department, phone FROM assistant";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n=== Assistant List ===");
            System.out.println("ID | Name | Department | Phone");
            System.out.println("--------------------------------");

            while (rs.next()) {
                int assistantId = rs.getInt("aid");
                String name = rs.getString("name");
                String department = rs.getString("department");
                String phone = rs.getString("phone");

                System.out.println(assistantId + " | " + name + " | " + department + " | " + phone);
            }

        } catch (Exception e) {
            System.out.println("Error occurred while fetching assistant list.");
            e.printStackTrace();
        }
    }

    private static boolean isValidStudent(Connection connection, int studentId) {
        String query = "SELECT COUNT(*) AS count FROM student WHERE sid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while validating student ID.");
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isStudentAlreadyPresident(Connection connection, int studentId) {
        String query = "SELECT COUNT(*) AS count FROM club WHERE president_sid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while checking student presidency.");
            e.printStackTrace();
        }
        return true;
    }

    private static boolean isValidProfessor(Connection connection, int professorId) {
        String query = "SELECT COUNT(*) AS count FROM professor WHERE pid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, professorId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while validating professor ID.");
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isProfessorAlreadyAdvisor(Connection connection, int professorId) {
        String query = "SELECT COUNT(*) AS count FROM club WHERE advisor_pid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, professorId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while checking professor advisorship.");
            e.printStackTrace();
        }
        return true;
    }
}
