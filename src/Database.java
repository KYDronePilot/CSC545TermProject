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
     * Run a parametrized SQL select query.
     *
     * @param sql query string
     * @param applyToRow lambda function to run on each returned row
     * @param setValues lambda function to bind parameters to prepared statement
     * @throws SQLException if there's an error running the query
     */
    public void select(
        String sql,
        ThrowingConsumer<ResultSet, SQLException> applyToRow,
        ThrowingConsumer<PreparedStatement, SQLException> setValues
    )
        throws SQLException {
        // Create a statement
        try (var stmt = conn.prepareStatement(sql)) {
            // Bind any statement parameters
            setValues.accept(stmt);
            // Run the query
            try (var rs = stmt.executeQuery()) {
                // For each result, call the `applyToRow` lambda, passing the row's ResultSet
                while (rs.next()) {
                    applyToRow.accept(rs);
                }
            }
        }
    }

    /**
     * Run a non-parametrized SQL select query.
     *
     * @param sql query string
     * @param applyToRow lambda function to run on each returned row
     * @throws SQLException if there's an error running the query
     */
    public void select(String sql, ThrowingConsumer<ResultSet, SQLException> applyToRow)
        throws SQLException {
        select(sql, applyToRow, stmt -> {});
    }

    /**
     * Run a parametrized SQL modifying query (e.g. one that doesn't return anything).
     *
     * @param sql modifying query string
     * @param setValues lambda function to bind parameters to prepared statement
     * @throws SQLException if there's an error running the query
     */
    public void modify(String sql, ThrowingConsumer<PreparedStatement, SQLException> setValues)
        throws SQLException {
        // Create a statement
        try (var stmt = conn.prepareStatement(sql)) {
            // Bind any parameters
            setValues.accept(stmt);
            // Execute it
            stmt.executeUpdate();
        }
    }

    /**
     * Run a non-parametrized SQL modifying query (e.g. one that doesn't return anything).
     *
     * @param sql modifying query string
     * @throws SQLException if there's an error running the query
     */
    public void modify(String sql) throws SQLException {
        modify(sql, stmt -> {});
    }
}
