package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for saving and retrieving results.
 * 
 * WHY a DAO?
 * It separates all our SQL queries from the rest of the application. 
 * If we ever want to switch from SQLite to MySQL, we only change code here!
 */
public class ResultDAO {

    /**
     * Saves a completed sort run into the database.
     */
    public static void saveResult(String algorithm, int arraySize, long timeTakenMs, int comparisons, int swaps) {
        String sql = "INSERT INTO sort_results(algorithm, array_size, time_taken_ms, comparisons, swaps) VALUES(?,?,?,?,?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set the parameters safely (prevents SQL injection)
            pstmt.setString(1, algorithm);
            pstmt.setInt(2, arraySize);
            pstmt.setLong(3, timeTakenMs);
            pstmt.setInt(4, comparisons);
            pstmt.setInt(5, swaps);
            
            pstmt.executeUpdate();
            System.out.println("Result saved to database!");

        } catch (SQLException e) {
            System.err.println("Error saving result: " + e.getMessage());
        }
    }

    /**
     * Retrieves all past results as a formatted Object array (perfect for a JTable).
     */
    public static Object[][] getAllResults() {
        String sql = "SELECT * FROM sort_results ORDER BY id DESC";
        List<Object[]> rows = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("algorithm"),
                    rs.getInt("array_size"),
                    rs.getInt("time_taken_ms") + " ms",
                    rs.getInt("comparisons"),
                    rs.getInt("swaps"),
                    rs.getString("run_date")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error fetching results: " + e.getMessage());
        }

        // Convert List to 2D array for JTable
        return rows.toArray(new Object[0][]);
    }
}
