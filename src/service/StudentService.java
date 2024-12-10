package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class StudentService {

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

    public static void viewMyClubs(int studentId, Connection connection) {
        String query = "SELECT c.name, c.location, c.is_academic FROM club c " +
                "JOIN clubmember cm ON c.cid = cm.cid WHERE cm.sid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n=== My Clubs ===");
                while (rs.next()) {
                    String clubName = rs.getString("name");
                    String location = rs.getString("location");
                    boolean isAcademic = rs.getBoolean("is_academic");
                    System.out.println("Name: " + clubName + ", Location: " + location +
                            ", Academic: " + (isAcademic ? "Yes" : "No"));
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while retrieving my clubs.");
            e.printStackTrace();
        }
    }

    public static void leaveClub(int studentId, Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=== Leave Club ===");
        System.out.print("Enter Club Name to leave: ");
        String clubName = scanner.nextLine();

        int clubId = getClubIdByName(clubName, connection);
        if (clubId == -1) {
            System.out.println("Club not found.");
            return;
        }

        if (!isAlreadyMember(studentId, clubId, connection)) {
            System.out.println("You are not a member of this club.");
            return;
        }

        if (isClubPresident(studentId, clubId, connection)) {
            System.out.println("You are the president of this club. You cannot leave the club.");
            return;
        }

        try {
            String deleteSQL = "DELETE FROM clubmember WHERE sid = ? AND cid = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
                stmt.setInt(1, studentId);
                stmt.setInt(2, clubId);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Successfully left the club.");
                } else {
                    System.out.println("Failed to leave the club.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while leaving the club.");
            e.printStackTrace();
        }
    }

    private static boolean isClubPresident(int studentId, int clubId, Connection connection) {
        String query = "SELECT COUNT(*) AS count FROM club WHERE president_sid = ? AND cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("count") > 0;
            }
        } catch (Exception e) {
            System.out.println("Error occurred while checking club presidency.");
            e.printStackTrace();
        }
        return false;
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
                return rs.next() && rs.getInt("count") > 0;
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
                return rs.next() && rs.getBoolean("is_academic");
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
                return rs.next() && rs.getInt("count") > 0;
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
