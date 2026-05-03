package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles the connection to our SQLite database.
 * 
 * WHY SQLite?
 * It's a serverless database that saves everything into a single local file.
 * Perfect for desktop applications! No complex MySQL setup needed.
 */
public class DBConnection {

    // The database file will be created in the project root
    private static final String URL = "jdbc:sqlite:results.db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found. Add it to your library path.");
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL);
    }

    /**
     * Initializes the database table if it doesn't exist yet.
     */
    public static void initDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS sort_results ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "algorithm TEXT NOT NULL,"
                + "array_size INTEGER NOT NULL,"
                + "time_taken_ms INTEGER NOT NULL,"
                + "comparisons INTEGER NOT NULL,"
                + "swaps INTEGER NOT NULL,"
                + "run_date DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Execute the SQL statement
            stmt.execute(createTableSQL);
            System.out.println("Database checked/initialized successfully.");
            
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
        }
    }
}
