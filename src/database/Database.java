package database;

import java.sql.*;
import java.util.Arrays;
import java.util.Optional;

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
        // Open connection
        try {
            conn = DriverManager.getConnection(DB_URL, Credentials.USERNAME, Credentials.PASSWORD);
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
    @Override
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
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Bind any statement parameters
            setValues.accept(stmt);
            // Run the query
            try (ResultSet rs = stmt.executeQuery()) {
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
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    /**
     * Insert a row into a table.
     *
     * Reference: how to get back auto-generated ID of row: https://stackoverflow.com/a/56236430/11354266
     * - Also: https://stackoverflow.com/a/1915197/11354266
     *
     * @param tableName name of the table
     * @param columns column names provided when inserting
     * @param setValues lambda to bind column values to query
     * @param getGeneratedKey should fetch id of newly generated row
     * @return id of new row if `getGeneratedKey == true`
     * @throws SQLException if error executing SQL
     */
    public Optional<Integer> insert(
        String tableName,
        String[] columns,
        ThrowingConsumer<PreparedStatement, SQLException> setValues,
        boolean getGeneratedKey
    )
        throws SQLException {
        // Generate placeholders for `VALUES` section of query
        String[] placeholders = new String[columns.length];
        Arrays.fill(placeholders, "?");
        // Generate insert statement
        try (
            PreparedStatement stmt = conn.prepareStatement(
                String.format(
                    "insert into %s (%s) values (%s)",
                    tableName,
                    String.join(",", columns),
                    String.join(",", placeholders)
                ),
                // If should return id, specify that it should be returned
                getGeneratedKey ? new String[] { "id" } : new String[] {}
            )
        ) {
            // Bind column values
            setValues.accept(stmt);
            // Execute insert
            stmt.executeUpdate();
            // Try to get the auto generated key if needed
            if (getGeneratedKey) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return Optional.of(rs.getInt(1));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Update the values of a table entry based on its id.
     *
     * @param tableName name of the table
     * @param columns column names to update
     * @param id id of the row to update
     * @param setValues lambda to bind column values to query
     * @throws SQLException if error executing SQL
     */
    public void update(
        String tableName,
        String[] columns,
        int id,
        ThrowingConsumer<PreparedStatement, SQLException> setValues
    )
        throws SQLException {
        // Generate array of the update command's `SET` parameters
        String[] updateAttrs = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            updateAttrs[i] = columns[i] + " = ?";
        }
        // Create update statement
        try (
            PreparedStatement stmt = conn.prepareStatement(
                String.format(
                    "update %s set %s where id = ?",
                    tableName,
                    String.join(",", updateAttrs)
                )
            )
        ) {
            // Bind id param
            stmt.setInt(columns.length + 1, id);
            // Bind column values
            setValues.accept(stmt);
            // Execute update
            stmt.executeUpdate();
        }
    }
}
