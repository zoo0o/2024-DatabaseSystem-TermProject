package Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class ViewClubMembers {

    public static void viewClubMembers(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Club Name: ");
        String clubName = scanner.nextLine();

        String queryClubPresident =
                "SELECT c.name AS club_name, s.name AS president_name, s.department AS president_department, s.phone AS president_phone, " +
                        "(SELECT COUNT(*) FROM clubmember cm JOIN club c2 ON cm.cid = c2.cid WHERE c2.name = ?) AS total_members " +
                        "FROM club c " +
                        "JOIN student s ON c.president_sid = s.sid " +
                        "WHERE c.name = ?";

        String queryClubMembers =
                "SELECT s.sid, s.name, s.department, s.status, s.phone, cm.join_date " +
                        "FROM club c " +
                        "JOIN clubmember cm ON c.cid = cm.cid " +
                        "JOIN student s ON cm.sid = s.sid " +
                        "WHERE c.name = ? " +
                        "ORDER BY cm.join_date";

        try {
            int totalMembers = 0;
            try (PreparedStatement stmtPresident = connection.prepareStatement(queryClubPresident)) {
                stmtPresident.setString(1, clubName);
                stmtPresident.setString(2, clubName);
                try (ResultSet rsPresident = stmtPresident.executeQuery()) {
                    if (rsPresident.next()) {
                        totalMembers = rsPresident.getInt("total_members");
                        System.out.println("\n=== Club: " + rsPresident.getString("club_name") + " ===");
                        System.out.println("Total Members: " + totalMembers);
                        System.out.println("President: " + rsPresident.getString("president_name") +
                                " | " + rsPresident.getString("president_department") +
                                " | " + rsPresident.getString("president_phone"));
                    } else {
                        System.out.println("Club not found.");
                        return;
                    }
                }
            }

            System.out.println("\nMembers:");
            System.out.println("ID | Name | Department | Status | Phone | Join Date");

            try (PreparedStatement stmtMembers = connection.prepareStatement(queryClubMembers)) {
                stmtMembers.setString(1, clubName);
                try (ResultSet rsMembers = stmtMembers.executeQuery()) {
                    boolean hasMembers = false;

                    while (rsMembers.next()) {
                        hasMembers = true;
                        int sid = rsMembers.getInt("sid");
                        String name = rsMembers.getString("name");
                        String department = rsMembers.getString("department");
                        String status = rsMembers.getString("status");
                        String phone = rsMembers.getString("phone");
                        String joinDate = rsMembers.getString("join_date");

                        System.out.println(sid + " | " + name + " | " + department + " | " + status + " | " + phone + " | " + joinDate);
                    }

                    if (!hasMembers) {
                        System.out.println("No members found for this club.");
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error occurred while fetching club members.");
            e.printStackTrace();
        }
    }
}
