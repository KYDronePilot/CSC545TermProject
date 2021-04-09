package models;

import database.Database;
import database.ThrowingConsumer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import utils.ModelHelper;

public class WeekMealPlan {

    public int id;
    public int mondayRecipeId;
    public int tuesdayRecipeId;
    public int wednesdayRecipeId;
    public int thursdayRecipeId;
    public int fridayRecipeId;
    public int saturdayRecipeId;
    public int sundayRecipeId;

    public WeekMealPlan(
        int id,
        int mondayRecipeId,
        int tuesdayRecipeId,
        int wednesdayRecipeId,
        int thursdayRecipeId,
        int fridayRecipeId,
        int saturdayRecipeId,
        int sundayRecipeId
    ) {
        this.id = id;
        this.mondayRecipeId = mondayRecipeId;
        this.tuesdayRecipeId = tuesdayRecipeId;
        this.wednesdayRecipeId = wednesdayRecipeId;
        this.thursdayRecipeId = thursdayRecipeId;
        this.fridayRecipeId = fridayRecipeId;
        this.saturdayRecipeId = saturdayRecipeId;
        this.sundayRecipeId = sundayRecipeId;
    }

    public static WeekMealPlan get(Integer id) throws SQLException {
        return ModelHelper.get(
            id,
            "WeekMealPlan",
            rs -> {
                return new WeekMealPlan(
                    id,
                    rs.getInt("mondayRecipeId"),
                    rs.getInt("tuesdayRecipeId"),
                    rs.getInt("wednesdayRecipeId"),
                    rs.getInt("thursdayRecipeId"),
                    rs.getInt("fridayRecipeId"),
                    rs.getInt("saturdayRecipeId"),
                    rs.getInt("sundayRecipeId")
                );
            }
        );
    }

    public static ArrayList<WeekMealPlan> filter(
        String sql,
        ThrowingConsumer<PreparedStatement, SQLException> setValues
    )
        throws SQLException {
        return ModelHelper.filter(
            sql,
            setValues,
            rs -> {
                return new WeekMealPlan(
                    rs.getInt("id"),
                    rs.getInt("mondayRecipeId"),
                    rs.getInt("tuesdayRecipeId"),
                    rs.getInt("wednesdayRecipeId"),
                    rs.getInt("thursdayRecipeId"),
                    rs.getInt("fridayRecipeId"),
                    rs.getInt("saturdayRecipeId"),
                    rs.getInt("sundayRecipeId")
                );
            }
        );
    }

    public static WeekMealPlan create(
        int mondayRecipeId,
        int tuesdayRecipeId,
        int wednesdayRecipeId,
        int thursdayRecipeId,
        int fridayRecipeId,
        int saturdayRecipeId,
        int sundayRecipeId
    )
        throws SQLException {
        var db = Database.getInstance();
        var id = db.insert(
            "WeekMealPlan",
            new String[] {
                "mondayRecipeId",
                "tuesdayRecipeId",
                "wednesdayRecipeId",
                "thursdayRecipeId",
                "fridayRecipeId",
                "saturdayRecipeId",
                "sundayRecipeId",
            },
            stmt -> {
                stmt.setInt(1, mondayRecipeId);
                stmt.setInt(2, tuesdayRecipeId);
                stmt.setInt(3, wednesdayRecipeId);
                stmt.setInt(4, thursdayRecipeId);
                stmt.setInt(5, fridayRecipeId);
                stmt.setInt(6, saturdayRecipeId);
                stmt.setInt(7, sundayRecipeId);
            },
            true
        );
        return get(id.get());
    }

    public void update() throws SQLException {
        var db = Database.getInstance();
        db.update(
            "WeekMealPlan",
            new String[] {
                "mondayRecipeId",
                "tuesdayRecipeId",
                "wednesdayRecipeId",
                "thursdayRecipeId",
                "fridayRecipeId",
                "saturdayRecipeId",
                "sundayRecipeId",
            },
            id,
            stmt -> {
                stmt.setInt(1, mondayRecipeId);
                stmt.setInt(2, tuesdayRecipeId);
                stmt.setInt(3, wednesdayRecipeId);
                stmt.setInt(4, thursdayRecipeId);
                stmt.setInt(5, fridayRecipeId);
                stmt.setInt(6, saturdayRecipeId);
                stmt.setInt(7, sundayRecipeId);
            }
        );
    }
}
