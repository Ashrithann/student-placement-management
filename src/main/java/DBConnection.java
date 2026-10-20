import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Reads connection settings from environment variables.
 * Defaults are for a local MySQL installation; set DB_PASSWORD before running.
 */
public final class DBConnection {
    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        String url = envOrDefault(
                "DB_URL", "jdbc:mysql://localhost:3306/placement_db");
        String user = envOrDefault("DB_USER", "root");
        String password = System.getenv("DB_PASSWORD");

        if (password == null) {
            password = "";
        }
        return DriverManager.getConnection(url, user, password);
    }

    private static String envOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}
