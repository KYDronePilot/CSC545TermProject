package models;

import database.Database;
import database.ThrowingConsumer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Optional;
import utils.ModelHelper;

public class DayMealPlan {

    public int id;
    public Optional<Integer> breakfastRecipeId;
    public Optional<Integer> lunchRecipeId;
    public Optional<Integer> dinnerRecipeId;

    public DayMealPlan(int id, int breakfastRecipeId, int lunchRecipeId, int dinnerRecipeId) {
        this.id = id;
        this.breakfastRecipeId = Optional.ofNullable(breakfastRecipeId);
        this.lunchRecipeId = Optional.ofNullable(lunchRecipeId);
        this.dinnerRecipeId = Optional.ofNullable(dinnerRecipeId);
    }

    public static DayMealPlan get(Integer id) throws SQLException {
        return ModelHelper.get(
            id,
            "DayMealPlan",
            rs -> {
                return new DayMealPlan(
                    id,
                    rs.getInt("breakfastRecipeId"),
                    rs.getInt("lunchRecipeId"),
                    rs.getInt("dinnerRecipeId")
                );
            }
        );
    }

    public static ArrayList<DayMealPlan> filter(
        String sql,
        ThrowingConsumer<PreparedStatement, SQLException> setValues
    )
        throws SQLException {
        return ModelHelper.filter(
            sql,
            setValues,
            rs -> {
                return new DayMealPlan(
                    rs.getInt("id"),
                    rs.getInt("breakfastRecipeId"),
                    rs.getInt("lunchRecipeId"),
                    rs.getInt("dinnerRecipeId")
                );
            }
        );
    }

    public static DayMealPlan create(
        Optional<Integer> breakfastRecipeId,
        Optional<Integer> lunchRecipeId,
        Optional<Integer> dinnerRecipeId
    )
        throws SQLException {
        var db = Database.getInstance();
        var id = db.insert(
            "DayMealPlan",
            new String[] { "breakfastRecipeId", "lunchRecipeId", "dinnerRecipeId" },
            stmt -> {
                if (breakfastRecipeId.isPresent()) {
                    stmt.setInt(1, breakfastRecipeId.get());
                } else {
                    stmt.setNull(1, Types.NULL);
                }
                if (lunchRecipeId.isPresent()) {
                    stmt.setInt(2, lunchRecipeId.get());
                } else {
                    stmt.setNull(2, Types.NULL);
                }
                if (dinnerRecipeId.isPresent()) {
                    stmt.setInt(3, dinnerRecipeId.get());
                } else {
                    stmt.setNull(3, Types.NULL);
                }
            },
            true
        );
        return get(id.get());
    }

    public void update() throws SQLException {
        var db = Database.getInstance();
        db.update(
            "DayMealPlan",
            new String[] { "breakfastRecipeId", "lunchRecipeId", "dinnerRecipeId" },
            id,
            stmt -> {
                if (breakfastRecipeId.isPresent()) {
                    stmt.setInt(1, breakfastRecipeId.get());
                } else {
                    stmt.setNull(1, Types.NULL);
                }
                if (lunchRecipeId.isPresent()) {
                    stmt.setInt(2, lunchRecipeId.get());
                } else {
                    stmt.setNull(2, Types.NULL);
                }
                if (dinnerRecipeId.isPresent()) {
                    stmt.setInt(3, dinnerRecipeId.get());
                } else {
                    stmt.setNull(3, Types.NULL);
                }
            }
        );
    }

    public void delete() throws SQLException {
        ModelHelper.delete(id, "DayMealPlan");
    }
}
