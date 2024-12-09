package Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewProfessors {

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
}
