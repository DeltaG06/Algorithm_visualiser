import ui.MainFrame;
import javax.swing.SwingUtilities;

/**
 * Main.java — The entry point of the application.
 *
 * WHY SwingUtilities.invokeLater()?
 * Swing is NOT thread-safe. All GUI updates must happen on the
 * "Event Dispatch Thread" (EDT). invokeLater() ensures our window
 * is created on that thread safely.
 */
import db.DBConnection;

public class Main {
    public static void main(String[] args) {
        // Initialize SQLite Database before starting the UI
        DBConnection.initDatabase();

        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}
