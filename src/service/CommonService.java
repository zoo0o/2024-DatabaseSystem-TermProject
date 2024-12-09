package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CommonService {

    public static void viewClubList(Connection connection) {
        String query = "SELECT cid, name, is_academic, location, president_sid, advisor_pid FROM club";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n=== Club List ===");
            System.out.println("ID | Name | Is Academic | Location | President | Advisor ");
            System.out.println("-------------------------------------------------------------------------------");

            while (rs.next()) {
                int clubId = rs.getInt("cid");
                String name = rs.getString("name");
                boolean isAcademic = rs.getBoolean("is_academic");
                String location = rs.getString("location");
                int presidentId = rs.getInt("president_sid");
                int advisorId = rs.getInt("advisor_pid");

                System.out.println(clubId + " | " + name + " | " + (isAcademic ? "Yes" : "No") + " | " + location
                        + " | " + presidentId + " | " + advisorId);
            }

        } catch (Exception e) {
            System.out.println("Error occurred while fetching club list.");
            e.printStackTrace();
        }
    }
}
