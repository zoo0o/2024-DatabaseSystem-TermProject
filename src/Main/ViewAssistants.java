package Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewAssistants {

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
}
