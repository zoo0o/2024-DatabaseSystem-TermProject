package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class PresidentService {

    public static void editClubInformation(int clubId, Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=== Edit Club Information ===");

        System.out.print("Enter new club name (leave blank to keep current): ");
        String newName = scanner.nextLine();

        System.out.print("Enter new location (leave blank to keep current): ");
        String newLocation = scanner.nextLine();

        try {
            StringBuilder sqlBuilder = new StringBuilder("UPDATE club SET ");
            boolean hasUpdates = false;

            if (!newName.isBlank()) {
                sqlBuilder.append("name = ?, ");
                hasUpdates = true;
            }
            if (!newLocation.isBlank()) {
                sqlBuilder.append("location = ?, ");
                hasUpdates = true;
            }

            if (!hasUpdates) {
                System.out.println("No changes were made.");
                return;
            }

            sqlBuilder.setLength(sqlBuilder.length() - 2);
            sqlBuilder.append(" WHERE cid = ?");

            String sql = sqlBuilder.toString();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                int paramIndex = 1;

                if (!newName.isBlank()) {
                    statement.setString(paramIndex++, newName);
                }
                if (!newLocation.isBlank()) {
                    statement.setString(paramIndex++, newLocation);
                }
                statement.setInt(paramIndex, clubId);

                int rows = statement.executeUpdate();
                if (rows > 0) {
                    System.out.println("Club information updated successfully.");
                } else {
                    System.out.println("Failed to update club information.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while updating club information.");
            e.printStackTrace();
        }
    }

    public static void viewMyClubMembers(int clubId, Connection connection) {
        String query = "SELECT s.sid, s.name, s.department, s.status, s.phone " +
                "FROM clubmember cm " +
                "JOIN student s ON cm.sid = s.sid " +
                "WHERE cm.cid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, clubId);

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n=== Members of Your Club ===");
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
}
