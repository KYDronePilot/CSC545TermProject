package utils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Lambda that takes a result set and constructs a model class instance.
 */
public interface ConstructInstanceLambda<T> {
    T run(ResultSet rs) throws SQLException;
}
