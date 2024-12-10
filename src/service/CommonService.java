package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CommonService {

    public static void viewClubList(Connection connection) {
        String query = """
                SELECT c.cid, c.name, c.is_academic, c.location, 
                       s.name AS president_name, 
                       p.name AS advisor_name
                FROM club c
                LEFT JOIN student s ON c.president_sid = s.sid
                LEFT JOIN professor p ON c.advisor_pid = p.pid
                """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n=== Club List ===");
            System.out.println("ID | Name | Is Academic | Location | President | Advisor");
            System.out.println("-------------------------------------------------------------------------------");

            while (rs.next()) {
                int clubId = rs.getInt("cid");
                String name = rs.getString("name");
                boolean isAcademic = rs.getBoolean("is_academic");
                String location = rs.getString("location");
                String presidentName = rs.getString("president_name");
                String advisorName = rs.getString("advisor_name");

                System.out.println(clubId + " | " + name + " | " + (isAcademic ? "Yes" : "No")
                        + " | " + location + " | " + (presidentName != null ? presidentName : "N/A")
                        + " | " + (advisorName != null ? advisorName : "N/A"));
            }

        } catch (Exception e) {
            System.out.println("Error occurred while fetching club list.");
            e.printStackTrace();
        }
    }
}
