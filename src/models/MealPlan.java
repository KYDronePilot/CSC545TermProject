package models;

import database.Database;
import database.ThrowingConsumer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import utils.ModelHelper;

public class MealPlan implements Comparable<MealPlan> {

    public Integer id;
    public String name;
    public String day;

    public MealPlan(Integer id, String name, String day) {
        this.id = id;
        this.name = name;
        this.day = day;
    }

    public static Optional<MealPlan> get(Integer id) throws SQLException {
        return Optional.ofNullable(
            ModelHelper.get(
                id,
                "MealPlan",
                rs -> {
                    return new MealPlan(id, rs.getString("name"), rs.getString("day"));
                }
            )
        );
    }

    public static ArrayList<MealPlan> filter(
        String sql,
        ThrowingConsumer<PreparedStatement, SQLException> setValues
    )
        throws SQLException {
        return ModelHelper.filter(
            sql,
            setValues,
            rs -> {
                return new MealPlan(rs.getInt("id"), rs.getString("name"), rs.getString("day"));
            }
        );
    }

    public static MealPlan create(String name, String day) throws SQLException {
        var db = Database.getInstance();
        var id = db.insert(
            "MealPlan",
            new String[] { "name", "day" },
            stmt -> {
                stmt.setString(1, name);
                stmt.setString(2, day);
            },
            true
        );
        return get(id.get()).get();
    }

    public void update() throws SQLException {
        var db = Database.getInstance();
        db.update(
            "MealPlan",
            new String[] { "name", "day" },
            id,
            stmt -> {
                stmt.setString(1, name);
                stmt.setString(2, day);
            }
        );
    }

    public void delete() throws SQLException {
        ModelHelper.delete(id, "MealPlan");
    }

    @Override
    public int compareTo(MealPlan that) {
        return this.id.compareTo(that.id);
    }
}
