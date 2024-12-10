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

    public static void manageDocuments(int clubId, Connection connection) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Manage Documents ===");
            System.out.println("1. Submit a New Document");
            System.out.println("2. Resubmit a Rejected Document");
            System.out.println("3. View All Documents");
            System.out.println("4. View Pending and Rejected Documents");
            System.out.println("5. Back to Students Options");
            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    submitNewDocument(clubId, connection);
                    break;
                case 2:
                    resubmitRejectedDocument(clubId, connection);
                    break;
                case 3:
                    viewAllDocuments(clubId, connection);
                    break;
                case 4:
                    viewPendingAndRejectedDocuments(clubId, connection);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid selection. Please try again.");
            }
        }
    }

    private static void submitNewDocument(int clubId, Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=== Submit a New Document ===");
        System.out.print("Enter document title: ");
        String title = scanner.nextLine();

        System.out.print("Enter document type: ");
        String type = scanner.nextLine();

        System.out.print("Enter document content: ");
        String content = scanner.nextLine();

        try {
            String sql = "INSERT INTO document (title, type, content, submit_cid, submit_date, is_approved) " +
                    "VALUES (?, ?, ?, ?, CURDATE(), FALSE)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, title);
                statement.setString(2, type);
                statement.setString(3, content);
                statement.setInt(4, clubId);

                int rows = statement.executeUpdate();
                if (rows > 0) {
                    System.out.println("Document submitted successfully.");
                } else {
                    System.out.println("Failed to submit the document.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while submitting the document.");
            e.printStackTrace();
        }
    }

    private static void resubmitRejectedDocument(int clubId, Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=== Resubmit a Rejected Document ===");

        try {
            System.out.print("Enter the document ID to resubmit: ");
            int documentId = scanner.nextInt();
            scanner.nextLine();

            String checkApprovalQuery = "SELECT content, submit_date, approve_date, is_approved, approve_aid " +
                    "FROM document WHERE did = ? AND submit_cid = ?";
            String content = null;

            try (PreparedStatement checkStatement = connection.prepareStatement(checkApprovalQuery)) {
                checkStatement.setInt(1, documentId);
                checkStatement.setInt(2, clubId);

                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        boolean isApproved = resultSet.getBoolean("is_approved");
                        String approveAid = resultSet.getString("approve_aid");

                        if (isApproved || approveAid == null) {
                            System.out.println("This document is not eligible for resubmission.");
                            return;
                        }

                        content = resultSet.getString("content");
                        System.out.println("\nCurrent Document Content:");
                        System.out.println(content);
                    } else {
                        System.out.println("Invalid document ID.");
                        return;
                    }
                }
            }

            System.out.print("\nEnter updated document content (Leave blank to cancel resubmission): ");
            String updatedContent = scanner.nextLine();

            if (updatedContent.isBlank()) {
                System.out.println("Content was not changed. Resubmission cancelled.");
                return;
            }

            String updateSql = "UPDATE document SET content = ?, submit_date = CURDATE(), approve_aid = NULL, approve_date = NULL " +
                    "WHERE did = ? AND submit_cid = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                updateStatement.setString(1, updatedContent);
                updateStatement.setInt(2, documentId);
                updateStatement.setInt(3, clubId);

                int rows = updateStatement.executeUpdate();
                if (rows > 0) {
                    System.out.println("Document resubmitted successfully.");
                } else {
                    System.out.println("Failed to resubmit the document.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while resubmitting the document.");
            e.printStackTrace();
        }
    }

    private static void viewAllDocuments(int clubId, Connection connection) {
        System.out.println("\n=== View All Documents ===");

        try {
            String query = "SELECT did, title, type, content, submit_date, approve_date, is_approved " +
                    "FROM document WHERE submit_cid = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, clubId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    System.out.println("ID | Title | Type | Content | Submit Date | Status");
                    while (resultSet.next()) {
                        int did = resultSet.getInt("did");
                        String title = resultSet.getString("title");
                        String type = resultSet.getString("type");
                        String content = resultSet.getString("content");
                        String submitDate = resultSet.getString("submit_date");
                        String approveDate = resultSet.getString("approve_date");
                        Boolean isApproved = resultSet.getBoolean("is_approved");

                        String status;
                        if (approveDate == null) {
                            status = "Pending";
                        } else if (isApproved) {
                            status = "Approved";
                        } else {
                            if (submitDate.compareTo(approveDate) > 0) {
                                status = "Pending (R)";
                            } else {
                                status = "Rejected";
                            }
                        }

                        System.out.println(did + " | " + title + " | " + type + " | " + content + " | " + submitDate + " | " + status);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while fetching all documents.");
            e.printStackTrace();
        }
    }

    private static void viewPendingAndRejectedDocuments(int clubId, Connection connection) {
        System.out.println("\n=== View Pending and Rejected Documents ===");

        try {
            String query = "SELECT did, title, type, content, submit_date, approve_date, is_approved " +
                    "FROM document WHERE submit_cid = ? AND is_approved = FALSE";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, clubId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    System.out.println("ID | Title | Type | Content | Submit Date | Status");
                    while (resultSet.next()) {
                        int did = resultSet.getInt("did");
                        String title = resultSet.getString("title");
                        String type = resultSet.getString("type");
                        String content = resultSet.getString("content");
                        String submitDate = resultSet.getString("submit_date");
                        String approveDate = resultSet.getString("approve_date");

                        String status;
                        if (approveDate == null) {
                            status = "Pending";
                        } else {
                            if (submitDate.compareTo(approveDate) > 0) {
                                status = "Pending (R)";
                            } else {
                                status = "Rejected";
                            }
                        }

                        System.out.println(did + " | " + title + " | " + type + " | " + content + " | " + submitDate + " | " + status);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while fetching pending and rejected documents.");
            e.printStackTrace();
        }
    }
}
