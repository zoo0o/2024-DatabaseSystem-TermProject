package Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewStudents {

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
}
