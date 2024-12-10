package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AssistantService {

    public static void createClub(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=== Create Club ===");
        System.out.print("Enter Club Name: ");
        String clubName = scanner.nextLine();

        System.out.print("Is this Academic Club? (y/n): ");
        String isAcademicInput = scanner.nextLine().toLowerCase();

        boolean isAcademic = isAcademicInput.equals("y");
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
            try (PreparedStatement insertClubStmt = connection.prepareStatement(insertClubSQL)) {
                insertClubStmt.setString(1, clubName);
                insertClubStmt.setBoolean(2, isAcademic);
                insertClubStmt.setString(3, location);
                insertClubStmt.setInt(4, presidentSid);
                insertClubStmt.setInt(5, advisorPid);

                insertClubStmt.executeUpdate();
                System.out.println("Club created successfully!");
            }
        } catch (Exception e) {
            System.out.println("Error occurred while creating club.");
            e.printStackTrace();
        }
    }

    public static void deleteClub(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=== Delete Club ===");
        System.out.print("Enter the Club ID to delete: ");
        int clubId = scanner.nextInt();
        scanner.nextLine();

        try {
            String checkClubQuery = "SELECT name FROM club WHERE cid = ?";
            String clubName = null;

            try (PreparedStatement checkStmt = connection.prepareStatement(checkClubQuery)) {
                checkStmt.setInt(1, clubId);
                try (ResultSet resultSet = checkStmt.executeQuery()) {
                    if (!resultSet.next()) {
                        System.out.println("Club not found with ID: " + clubId);
                        return;
                    } else {
                        clubName = resultSet.getString("name");
                        System.out.println("Found Club: " + clubName);
                    }
                }
            }

            System.out.print("To confirm deletion, type the club name exactly as shown: ");
            String confirmationName = scanner.nextLine();

            if (!confirmationName.equals(clubName)) {
                System.out.println("Club name mismatch. Deletion canceled.");
                return;
            }

            try {
                connection.setAutoCommit(false);

                String deleteClubMembersQuery = "DELETE FROM clubmember WHERE cid = ?";
                try (PreparedStatement deleteMembersStmt = connection.prepareStatement(deleteClubMembersQuery)) {
                    deleteMembersStmt.setInt(1, clubId);
                    deleteMembersStmt.executeUpdate();
                }

                String deleteDocumentsQuery = "DELETE FROM document WHERE submit_cid = ?";
                try (PreparedStatement deleteDocsStmt = connection.prepareStatement(deleteDocumentsQuery)) {
                    deleteDocsStmt.setInt(1, clubId);
                    deleteDocsStmt.executeUpdate();
                }

                String deleteClubQuery = "DELETE FROM club WHERE cid = ?";
                try (PreparedStatement deleteClubStmt = connection.prepareStatement(deleteClubQuery)) {
                    deleteClubStmt.setInt(1, clubId);
                    int rowsDeleted = deleteClubStmt.executeUpdate();
                    if (rowsDeleted > 0) {
                        System.out.println("Club and related data deleted successfully.");
                    } else {
                        System.out.println("Failed to delete the club.");
                    }
                }

                connection.commit(); // Commit transaction
            } catch (Exception e) {
                connection.rollback(); // Rollback transaction on error
                System.out.println("Error occurred while deleting club. Transaction rolled back.");
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            System.out.println("Error occurred while deleting club.");
            e.printStackTrace();
        }
    }

    public static void viewClubMembers(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Club Name: ");
        String clubName = scanner.nextLine();

        String checkClubQuery = "SELECT COUNT(*) AS count FROM club WHERE name = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkClubQuery)) {
            checkStmt.setString(1, clubName);
            try (ResultSet resultSet = checkStmt.executeQuery()) {
                if (resultSet.next() && resultSet.getInt("count") == 0) {
                    System.out.println("The club '" + clubName + "' does not exist.");
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while checking club existence.");
            e.printStackTrace();
            return;
        }

        String query = "SELECT s.sid, s.name, s.department, s.status, s.phone " +
                "FROM clubmember cm " +
                "JOIN student s ON cm.sid = s.sid " +
                "JOIN club c ON cm.cid = c.cid WHERE c.name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, clubName);

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n=== Members of " + clubName + " ===");
                System.out.println("ID | Name | Department | Status | Phone");
                boolean hasMembers = false;

                while (rs.next()) {
                    hasMembers = true;
                    int studentId = rs.getInt("sid");
                    String name = rs.getString("name");
                    String department = rs.getString("department");
                    String status = rs.getString("status");
                    String phone = rs.getString("phone");

                    System.out.println(studentId + " | " + name + " | " + department + " | " + status + " | " + phone);
                }

                if (!hasMembers) {
                    System.out.println("No members found in this club.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while fetching club members.");
            e.printStackTrace();
        }
    }


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

    private static boolean isValidStudent(Connection connection, int studentId) {
        String query = "SELECT COUNT(*) AS count FROM student WHERE sid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while validating student ID.");
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isStudentAlreadyPresident(Connection connection, int studentId) {
        String query = "SELECT COUNT(*) AS count FROM club WHERE president_sid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while checking student presidency.");
            e.printStackTrace();
        }
        return true;
    }

    private static boolean isValidProfessor(Connection connection, int professorId) {
        String query = "SELECT COUNT(*) AS count FROM professor WHERE pid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, professorId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while validating professor ID.");
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isProfessorAlreadyAdvisor(Connection connection, int professorId) {
        String query = "SELECT COUNT(*) AS count FROM club WHERE advisor_pid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, professorId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while checking professor advisorship.");
            e.printStackTrace();
        }
        return true;
    }

    public static void manageDocuments(int assistantId, Connection connection) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Manage Documents ===");
            System.out.println("1. View All Documents");
            System.out.println("2. View Pending Documents");
            System.out.println("3. Approve or Reject Documents");
            System.out.println("4. View Documents I Approved or Rejected");
            System.out.println("5. Back to Assistant Options");
            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    viewAllDocuments(connection);
                    break;
                case 2:
                    viewPendingDocuments(connection);
                    break;
                case 3:
                    approveOrRejectDocument(assistantId, connection);
                    break;
                case 4:
                    viewMyApprovedOrRejectedDocuments(assistantId, connection);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid selection.");
            }
        }
    }

    private static void viewAllDocuments(Connection connection) {
        System.out.println("\n=== View All Documents ===");

        try {
            String query = "SELECT did, title, type, content, submit_date, approve_date, approve_aid, is_approved " +
                    "FROM document";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                System.out.println("ID | Title | Type | Content | Submit Date | Approve Date | Status | Approved By");
                while (resultSet.next()) {
                    int did = resultSet.getInt("did");
                    String title = resultSet.getString("title");
                    String type = resultSet.getString("type");
                    String content = resultSet.getString("content");
                    String submitDate = resultSet.getString("submit_date");
                    String approveDate = resultSet.getString("approve_date");
                    String approveAid = resultSet.getString("approve_aid");
                    Boolean isApproved = resultSet.getBoolean("is_approved");

                    String status;
                    if (approveDate == null) {
                        status = (approveAid == null) ? "Pending (Initial)" : "Pending (Resubmitted)";
                    } else if (isApproved) {
                        status = "Approved";
                    } else {
                        status = "Rejected";
                    }

                    System.out.println(did + " | " + title + " | " + type + " | " + content + " | " + submitDate + " | " +
                            (approveDate != null ? approveDate : "None") + " | " + status + " | " + (approveAid != null ? approveAid : "None"));
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while fetching all documents.");
            e.printStackTrace();
        }
    }

    private static void viewPendingDocuments(Connection connection) {
        System.out.println("\n=== View Pending Documents ===");

        try {
            String query = "SELECT did, title, type, content, submit_date, approve_aid " +
                    "FROM document WHERE approve_date IS NULL";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                System.out.println("ID | Title | Type | Content | Submit Date | Status | Approved By");
                while (resultSet.next()) {
                    int did = resultSet.getInt("did");
                    String title = resultSet.getString("title");
                    String type = resultSet.getString("type");
                    String content = resultSet.getString("content");
                    String submitDate = resultSet.getString("submit_date");
                    String approveAid = resultSet.getString("approve_aid");

                    String status = (approveAid == null) ? "Pending (Initial)" : "Pending (Resubmitted)";

                    System.out.println(did + " | " + title + " | " + type + " | " + content + " | " + submitDate + " | " + status + " | " + (approveAid != null ? approveAid : "None"));
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while fetching pending documents.");
            e.printStackTrace();
        }
    }

    private static void approveOrRejectDocument(int assistantId, Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nEnter the document ID to approve or reject: ");
        int documentId = scanner.nextInt();
        scanner.nextLine();

        try {
            String checkQuery = "SELECT approve_aid, submit_date, approve_date " +
                    "FROM document WHERE did = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setInt(1, documentId);

                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String approveAid = resultSet.getString("approve_aid");
                        String submitDate = resultSet.getString("submit_date");
                        String approveDate = resultSet.getString("approve_date");

                        if (approveDate == null) {
                            if (approveAid != null && Integer.parseInt(approveAid) != assistantId) {
                                System.out.println("This resubmitted document can only be processed by the assistant who rejected it previously.");
                                return;
                            }
                        } else {
                            System.out.println("This document is not pending approval or already processed.");
                            return;
                        }
                    } else {
                        System.out.println("Document not found.");
                        return;
                    }
                }
            }

            System.out.println("1. Approve");
            System.out.println("2. Reject");
            System.out.print("Select an action: ");
            int action = scanner.nextInt();
            scanner.nextLine();

            String updateQuery = "UPDATE document SET approve_aid = ?, approve_date = NOW(), is_approved = ? WHERE did = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setInt(1, assistantId);
                updateStatement.setBoolean(2, action == 1);
                updateStatement.setInt(3, documentId);

                int rows = updateStatement.executeUpdate();
                if (rows > 0) {
                    System.out.println(action == 1 ? "Document approved successfully." : "Document rejected successfully.");
                } else {
                    System.out.println("Failed to update document status.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while approving/rejecting the document.");
            e.printStackTrace();
        }
    }

    private static void viewMyApprovedOrRejectedDocuments(int assistantId, Connection connection) {
        System.out.println("\n=== View Documents I Approved or Rejected ===");

        try {
            String query = "SELECT did, title, type, content, submit_date, approve_date, is_approved " +
                    "FROM document WHERE approve_aid = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, assistantId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    System.out.println("ID | Title | Type | Content | Submit Date | Approve Date | Status");
                    while (resultSet.next()) {
                        int did = resultSet.getInt("did");
                        String title = resultSet.getString("title");
                        String type = resultSet.getString("type");
                        String content = resultSet.getString("content");
                        String submitDate = resultSet.getString("submit_date");
                        String approveDate = resultSet.getString("approve_date");
                        Boolean isApproved = resultSet.getBoolean("is_approved");

                        String status = isApproved ? "Approved" : "Rejected";

                        System.out.println(did + " | " + title + " | " + type + " | " + content + " | " + submitDate + " | " +
                                approveDate + " | " + status);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while fetching documents approved or rejected by you.");
            e.printStackTrace();
        }
    }
}
