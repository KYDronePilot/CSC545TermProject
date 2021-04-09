package models;

import database.Database;
import database.ThrowingConsumer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import utils.ModelHelper;

public class DayMealPlan {

    public int id;
    public int breakfastRecipeId;
    public int lunchRecipeId;
    public int dinnerRecipeId;

    public DayMealPlan(int id, int breakfastRecipeId, int lunchRecipeId, int dinnerRecipeId) {
        this.id = id;
        this.breakfastRecipeId = breakfastRecipeId;
        this.lunchRecipeId = lunchRecipeId;
        this.dinnerRecipeId = dinnerRecipeId;
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

    public static DayMealPlan create(int breakfastRecipeId, int lunchRecipeId, int dinnerRecipeId)
        throws SQLException {
        var db = Database.getInstance();
        var id = db.insert(
            "DayMealPlan",
            new String[] { "breakfastRecipeId", "lunchRecipeId", "dinnerRecipeId" },
            stmt -> {
                stmt.setInt(1, breakfastRecipeId);
                stmt.setInt(2, lunchRecipeId);
                stmt.setInt(3, dinnerRecipeId);
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
                stmt.setInt(1, breakfastRecipeId);
                stmt.setInt(2, lunchRecipeId);
                stmt.setInt(3, dinnerRecipeId);
            }
        );
    }

    public void delete() throws SQLException {
        ModelHelper.delete(id, "DayMealPlan");
    }
}
