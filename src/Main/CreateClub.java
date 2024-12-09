package Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class CreateClub {

    public static void createClub(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=== Create Club ===");
        System.out.print("Enter Club Name: ");
        String clubName = scanner.nextLine();

        System.out.print("Is this Academic Club? (y/n): ");
        String isAcademicInput = scanner.nextLine().toLowerCase();

        boolean isAcademic;
        if (isAcademicInput.equals("y")) {
            isAcademic = true;
        } else if (isAcademicInput.equals("n")) {
            isAcademic = false;
        } else {
            System.out.println("Invalid input.");
            return;
        }

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
            int clubId;
            try (PreparedStatement insertClubStmt = connection.prepareStatement(insertClubSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertClubStmt.setString(1, clubName);
                insertClubStmt.setBoolean(2, isAcademic);
                insertClubStmt.setString(3, location);
                insertClubStmt.setInt(4, presidentSid);
                insertClubStmt.setInt(5, advisorPid);

                int affectedRows = insertClubStmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating club failed, no rows affected.");
                }

                try (ResultSet generatedKeys = insertClubStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        clubId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating club failed, no ID obtained.");
                    }
                }
            }

            String insertClubMemberSQL = "INSERT INTO clubmember (sid, cid, join_date) VALUES (?, ?, CURRENT_DATE)";
            try (PreparedStatement insertClubMemberStmt = connection.prepareStatement(insertClubMemberSQL)) {
                insertClubMemberStmt.setInt(1, presidentSid);
                insertClubMemberStmt.setInt(2, clubId);
                insertClubMemberStmt.executeUpdate();
            }

            System.out.println("Club created successfully!");

        } catch (Exception e) {
            System.out.println("Error occurred while creating club.");
            e.printStackTrace();
        }
    }

    private static boolean isValidStudent(Connection connection, int studentId) {
        try {
            String query = "SELECT COUNT(*) AS count FROM student WHERE sid = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, studentId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("count") > 0;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while validating student ID.");
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isStudentAlreadyPresident(Connection connection, int studentId) {
        try {
            String query = "SELECT COUNT(*) AS count FROM club WHERE president_sid = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, studentId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("count") > 0;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while checking student.");
            e.printStackTrace();
        }
        return true;
    }

    private static boolean isValidProfessor(Connection connection, int professorId) {
        try {
            String query = "SELECT COUNT(*) AS count FROM professor WHERE pid = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, professorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("count") > 0;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while validating professor ID.");
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isProfessorAlreadyAdvisor(Connection connection, int professorId) {
        try {
            String query = "SELECT COUNT(*) AS count FROM club WHERE advisor_pid = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, professorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("count") > 0;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while checking professor.");
            e.printStackTrace();
        }
        return true;
    }
}
