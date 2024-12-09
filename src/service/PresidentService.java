package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class PresidentService {

    public static void editClubInformation(int clubId, Connection connection) {
        Scanner scanner = new Scanner(System.in);

        String newName = null;
        String newLocation = null;

        while (true) {
            System.out.println("\n=== Edit Club Information ===");
            System.out.println("1. Change Club Name");
            System.out.println("2. Change Club Location");
            System.out.println("3. Save and Exit");
            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    System.out.print("Enter new club name (leave blank to keep current): ");
                    newName = scanner.nextLine();
                    break;
                case 2:
                    System.out.print("Enter new location (leave blank to keep current): ");
                    newLocation = scanner.nextLine();
                    break;
                case 3:
                    try {
                        StringBuilder sqlBuilder = new StringBuilder("UPDATE club SET ");
                        boolean hasUpdates = false;

                        if (newName != null && !newName.isEmpty()) {
                            sqlBuilder.append("name = ?, ");
                            hasUpdates = true;
                        }
                        if (newLocation != null && !newLocation.isEmpty()) {
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

                            if (newName != null && !newName.isEmpty()) {
                                statement.setString(paramIndex++, newName);
                            }
                            if (newLocation != null && !newLocation.isEmpty()) {
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
                    return;
                default:
                    System.out.println("Invalid selection. Please try again.");
            }
        }
    }
}
