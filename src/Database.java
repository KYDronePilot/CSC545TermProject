import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;

/**
 * Database interaction wrapper.
 *
 * Singleton pattern based on this example: https://www.geeksforgeeks.org/singleton-class-java/
 * Uses try with resource syntax to auto-close when done: https://stackoverflow.com/a/15768083/11354266
 */
public class Database implements AutoCloseable {

    // Singleton database instance
    private static Database instance = null;

    static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    static final String DB_URL = "jdbc:oracle:thin:@157.89.28.130:1521:cscdb";

    // Persistent database connection
    Connection conn;

    /**
     * Setup the database connection.
     *
     * @throws SQLException if there's an error connecting to the database
     */
    private Database() throws SQLException {
        // Register the Oracle JDBC driver
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            // Can't start program if no JDBC drivers are installed
            System.out.println("JDBC driver not installed");
            System.exit(1);
        }
        // For getting DB credentials from .env file
        var env = Dotenv.load();
        // Open connection
        try {
            conn = DriverManager.getConnection(DB_URL, env.get("USERNAME"), env.get("PASSWORD"));
        } catch (SQLException e) {
            System.out.println("Failed to connect to DB");
            throw e;
        }
    }

    /**
     * Get or create the shared database singleton instance.
     *
     * @return database instance
     * @throws SQLException if there's an error connecting to the database
     */
    public static Database getInstance() throws SQLException {
        // Create instance if one doesn't exist
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * Close the shared database connection if it is open.
     */
    public void close() {
        if (instance != null) {
            // Try to close, but proceed even if there's an error
            try {
                instance.conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing DB connection, but proceeding");
            }
            instance = null;
        }
    }

    /**
     * Run a SQL select query.
     *
     * @param sql query string
     * @param applyToRow lambda function to run for each returned row
     * @throws SQLException if there's an error running the query
     */
    public void select(String sql, ThrowingConsumer<ResultSet, SQLException> applyToRow)
        throws SQLException {
        // Create a statement and execute the query
        try (var stmt = conn.createStatement()) {
            try (var rs = stmt.executeQuery(sql)) {
                // For each result, call the `applyToRow` lambda, passing a ResultSet instance
                while (rs.next()) {
                    applyToRow.accept(rs);
                }
            }
        }
    }
}
