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

    public static void viewClubMembers(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Club Name: ");
        String clubName = scanner.nextLine();

        String query = "SELECT s.sid, s.name, s.department, s.status, s.phone " +
                "FROM clubmember cm " +
                "JOIN student s ON cm.sid = s.sid " +
                "JOIN club c ON cm.cid = c.cid WHERE c.name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, clubName);

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n=== Members of " + clubName + " ===");
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
            System.out.println("3. View My Approved Documents");
            System.out.println("4. Approve or Reject Document");
            System.out.println("5. Back to Options");
            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    viewAllDocuments(connection);
                    break;
                case 2:
                    viewPendingAndRejectedDocuments(connection);
                    break;
                case 3:
                    viewMyApprovedDocuments(assistantId, connection);
                    break;
                case 4:
                    approveOrRejectDocument(assistantId, connection);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid selection.");
            }
        }

    }

    public static void viewAllDocuments(Connection connection) {
        String query = "SELECT d.did, d.title, d.type, d.content, s.submit_date, " +
                "       CASE " +
                "           WHEN a.is_approved IS NULL THEN 'Pending' " +
                "           WHEN a.is_approved = 0 THEN 'Rejected' " +
                "           WHEN a.is_approved = 1 THEN 'Approved' " +
                "       END AS approval_status, " +
                "       a.aid AS approved_by, a.approve_date " +
                "FROM document d " +
                "LEFT JOIN submit s ON d.did = s.did " +
                "LEFT JOIN approve a ON d.did = a.did " +
                "WHERE s.submit_date IS NOT NULL " +
                "ORDER BY s.submit_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n=== All Documents ===");
            System.out.println("ID | Title | Type | Content | Submit Date | Approval Status | Approved By | Approval Date");
            while (rs.next()) {
                int docId = rs.getInt("did");
                String title = rs.getString("title");
                String type = rs.getString("type");
                String content = rs.getString("content");
                String submitDate = rs.getString("submit_date");
                String approvalStatus = rs.getString("approval_status");
                String approvedBy = rs.getString("approved_by") == null ? "N/A" : rs.getString("approved_by");
                String approvalDate = rs.getString("approve_date") == null ? "N/A" : rs.getString("approve_date");

                System.out.println(docId + " | " + title + " | " + type + " | " + content + " | " + submitDate + " | " + approvalStatus + " | " + approvedBy + " | " + approvalDate);
            }
        } catch (Exception e) {
            System.out.println("Error occurred while fetching all documents.");
            e.printStackTrace();
        }
    }

    public static void viewPendingAndRejectedDocuments(Connection connection) {
        String query = "SELECT d.did, d.title, s.cid, s.submit_date, " +
                "       CASE " +
                "           WHEN a.is_approved IS NULL THEN 'Pending' " +
                "           WHEN a.is_approved = 0 THEN 'Rejected' " +
                "       END AS approval_status, " +
                "       a.aid AS approved_by " +
                "FROM document d " +
                "JOIN submit s ON d.did = s.did " +
                "LEFT JOIN approve a ON d.did = a.did " +
                "WHERE (a.is_approved IS NULL OR a.is_approved = 0) " +
                "AND s.submit_date IS NOT NULL " +
                "ORDER BY s.submit_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n=== Pending and Rejected Documents ===");
            System.out.println("ID | Title | Club ID | Submit Date | Approval Status | Approved By");
            while (rs.next()) {
                int docId = rs.getInt("did");
                String title = rs.getString("title");
                int clubId = rs.getInt("cid");
                String submitDate = rs.getString("submit_date");
                String approvalStatus = rs.getString("approval_status");
                String approvedBy = rs.getString("approved_by");

                System.out.println(docId + " | " + title + " | " + clubId + " | " + submitDate + " | " + approvalStatus + " | " + (approvedBy == null ? "N/A" : approvedBy));
            }
        } catch (Exception e) {
            System.out.println("Error occurred while fetching pending/rejected documents.");
            e.printStackTrace();
        }
    }

    public static void viewMyApprovedDocuments(int assistantId, Connection connection) {
        String query = "SELECT d.did, d.title, d.type, d.content, s.submit_date, a.is_approved, a.aid, a.approve_date " +
                "FROM document d " +
                "JOIN submit s ON d.did = s.did " +
                "LEFT JOIN approve a ON d.did = a.did " +
                "WHERE a.aid = ? AND a.is_approved = 1 AND s.submit_date IS NOT NULL " +
                "ORDER BY s.submit_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, assistantId);

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n=== My Approved Documents ===");
                System.out.println("ID | Title | Type | Content | Submit Date | Approval Status | Approved By | Approval Date");
                while (rs.next()) {
                    int docId = rs.getInt("did");
                    String title = rs.getString("title");
                    String type = rs.getString("type");
                    String content = rs.getString("content");
                    String submitDate = rs.getString("submit_date");
                    String approvalStatus = (rs.getInt("is_approved") == 1) ? "Approved" : "Rejected";
                    String approvedBy = rs.getString("aid") == null ? "N/A" : rs.getString("aid");
                    String approvalDate = rs.getString("approve_date");

                    System.out.println(docId + " | " + title + " | " + type + " | " + content + " | " + submitDate + " | " + approvalStatus + " | " + approvedBy + " | " + approvalDate);
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while fetching documents.");
            e.printStackTrace();
        }
    }

    public static void approveOrRejectDocument(int assistantId, Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=== Approve or Reject Document ===");
        System.out.print("Enter Document ID: ");
        int documentId = scanner.nextInt();
        scanner.nextLine();

        String fetchQuery = "SELECT did, title, type, content, s.submit_date FROM document d " +
                "JOIN submit s ON d.did = s.did WHERE d.did = ?";

        try (PreparedStatement fetchStmt = connection.prepareStatement(fetchQuery)) {
            fetchStmt.setInt(1, documentId);

            try (ResultSet rs = fetchStmt.executeQuery()) {
                if (rs.next()) {
                    String submitDate = rs.getString("submit_date");
                    if (submitDate == null) {
                        System.out.println("This document has not been submitted yet and cannot be approved or rejected.");
                        return;
                    }

                    int docId = rs.getInt("did");
                    String title = rs.getString("title");
                    String type = rs.getString("type");
                    String content = rs.getString("content");

                    System.out.println("\n=== Document Details ===");
                    System.out.println("Document ID: " + docId);
                    System.out.println("Title: " + title);
                    System.out.println("Type: " + type);
                    System.out.println("Content: " + content);

                    String checkApprovalQuery = "SELECT aid, is_approved FROM approve WHERE did = ?";
                    try (PreparedStatement checkStmt = connection.prepareStatement(checkApprovalQuery)) {
                        checkStmt.setInt(1, documentId);

                        try (ResultSet approvalResult = checkStmt.executeQuery()) {
                            if (approvalResult.next()) {
                                boolean isApproved = approvalResult.getBoolean("is_approved");

                                if (isApproved) {
                                    System.out.println("This document has already been approved and cannot be approved again.");
                                    return;
                                }
                                int lastAssistantId = approvalResult.getInt("aid");

                                if (isApproved == false && lastAssistantId != assistantId) {
                                    System.out.println("Only the assistant who rejected this document can approve it.");
                                    return;
                                }
                            }
                        }
                    }

                    System.out.println("\n1. Approve");
                    System.out.println("2. Reject");
                    System.out.print("Select an action: ");
                    int action = scanner.nextInt();
                    scanner.nextLine();

                    boolean isApproved = action == 1;

                    String approveQuery = "INSERT INTO approve (did, aid, approve_date, is_approved) " +
                            "VALUES (?, ?, CURDATE(), ?) " +
                            "ON DUPLICATE KEY UPDATE is_approved = ?";
                    try (PreparedStatement approveStmt = connection.prepareStatement(approveQuery)) {
                        approveStmt.setInt(1, documentId);
                        approveStmt.setInt(2, assistantId);
                        approveStmt.setBoolean(3, isApproved);
                        approveStmt.setBoolean(4, isApproved);

                        int rows = approveStmt.executeUpdate();
                        if (rows > 0) {
                            System.out.println(isApproved ? "Document approved successfully!" : "Document rejected successfully!");
                        } else {
                            System.out.println("Failed to process document.");
                        }
                    }
                } else {
                    System.out.println("Document with ID " + documentId + " does not exist.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while fetching or processing the document.");
            e.printStackTrace();
        }
    }

}
