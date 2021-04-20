package utils;

import database.Database;
import database.ThrowingConsumer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Object that contains another object.
 *
 * Used for temporarily storing object constructed in lambda so it can be returned later from outer
 * scope.
 */
class Container<T> {

    public T obj;

    public Container(T obj) {
        this.obj = obj;
    }
}

/**
 * Helper methods for database model operations.
 */
public class ModelHelper {

    /**
     * Helper for getting a single database row by id.
     *
     * @param <T> the model class type
     * @param id row id to get
     * @param className name of the model class
     * @param constructInstance lambda to construct model class instance from result set
     * @return new model class instance
     * @throws SQLException if error executing SQL
     */
    public static <T> T get(
        Integer id,
        String className,
        ConstructInstanceLambda<T> constructInstance
    )
        throws SQLException {
        var db = Database.getInstance();
        // Container object to temporarily store instance in so it can later be returned
        var container = new Container<T>(null);
        // Query by id
        db.select(
            String.format("select * from %s where id = ?", className),
            rs -> {
                // Construct and store model instance in container
                container.obj = constructInstance.run(rs);
            },
            stmt -> {
                // Bind id to query
                stmt.setInt(1, id);
            }
        );
        // Return instance in container
        return container.obj;
    }

    /**
     * Helper for filtering rows by a SQL query.
     *
     * @param <T> the model class type
     * @param sql query to run (must select all columns from model relation)
     * @param setValues lambda to bind query parameters
     * @param constructInstance lambda to construct model class instance from result set
     * @return new model class instance
     * @throws SQLException if error executing SQL
     */
    public static <T extends Comparable<T>> ArrayList<T> filter(
        String sql,
        ThrowingConsumer<PreparedStatement, SQLException> setValues,
        ConstructInstanceLambda<T> constructInstance
    )
        throws SQLException {
        var db = Database.getInstance();
        // For storing query results
        var results = new ArrayList<T>();
        // Run query
        db.select(
            sql,
            rs -> {
                // Construct and add instance to list of results
                results.add(constructInstance.run(rs));
            },
            setValues
        );
        Collections.sort(results);
        return results;
    }

    /**
     * Helper for deleting a table entry by id.
     *
     * @param id id of the entry
     * @param tableName name of the table to delete from
     * @throws SQLException if error executing SQL
     */
    public static void delete(Integer id, String tableName) throws SQLException {
        var db = Database.getInstance();
        // Run query
        db.modify(
            String.format("delete from %s where id = ?", tableName),
            stmt -> {
                stmt.setInt(1, id);
            }
        );
    }
}
