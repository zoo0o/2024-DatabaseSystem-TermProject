package Main;


public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Initializing database...");
            DatabaseSetup.initialize();
            System.out.println("Database initialized successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
