package Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class JoinClub {

    public static void joinClub(int studentId, Connection connection) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("\n=== Join Club ===");
            System.out.print("Enter Club Name: ");
            String clubName = scanner.nextLine();

            int clubId = getClubIdByName(clubName, connection);
            if (clubId == -1) {
                System.out.println("Club not found.");
                return;
            }

            if (isAlreadyMember(studentId, clubId, connection)) {
                System.out.println("You are already a member of this club.");
                return;
            }

            if (isAcademicClub(clubId, connection) && hasAcademicClub(studentId, connection)) {
                System.out.println("You are already a member of an academic club.");
                return;
            }

            addStudentToClub(studentId, clubId, connection);

        } catch (Exception e) {
            System.out.println("Error occurred while joining the club.");
            e.printStackTrace();
        }
    }

    private static int getClubIdByName(String clubName, Connection connection) {
        String query = "SELECT cid FROM club WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, clubName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cid");
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while retrieving club ID.");
            e.printStackTrace();
        }
        return -1;
    }

    private static boolean isAlreadyMember(int studentId, int clubId, Connection connection) {
        String query = "SELECT COUNT(*) AS count FROM clubmember WHERE sid = ? AND cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt("count") > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while checking membership.");
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isAcademicClub(int clubId, Connection connection) {
        String query = "SELECT is_academic FROM club WHERE cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_academic");
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while checking club type.");
            e.printStackTrace();
        }
        return false;
    }

    private static boolean hasAcademicClub(int studentId, Connection connection) {
        String query = "SELECT COUNT(*) AS count FROM clubmember cm " +
                "JOIN club c ON cm.cid = c.cid " +
                "WHERE cm.sid = ? AND c.is_academic = true";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt("count") > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while checking academic club membership.");
            e.printStackTrace();
        }
        return false;
    }

    private static void addStudentToClub(int studentId, int clubId, Connection connection) {
        String insertSQL = "INSERT INTO clubmember (sid, cid, join_date) VALUES (?, ?, CURRENT_DATE)";
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, clubId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Successfully joined the club!");
            } else {
                System.out.println("Failed to join the club.");
            }
        } catch (Exception e) {
            System.out.println("Error occurred while adding to club.");
            e.printStackTrace();
        }
    }
}
