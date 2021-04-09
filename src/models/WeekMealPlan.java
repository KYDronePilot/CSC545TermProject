package models;

import database.Database;
import database.ThrowingConsumer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Optional;
import utils.ModelHelper;

public class WeekMealPlan {

    public int id;
    public Optional<Integer> mondayRecipeId;
    public Optional<Integer> tuesdayRecipeId;
    public Optional<Integer> wednesdayRecipeId;
    public Optional<Integer> thursdayRecipeId;
    public Optional<Integer> fridayRecipeId;
    public Optional<Integer> saturdayRecipeId;
    public Optional<Integer> sundayRecipeId;

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
        this.mondayRecipeId = Optional.ofNullable(mondayRecipeId);
        this.tuesdayRecipeId = Optional.ofNullable(tuesdayRecipeId);
        this.wednesdayRecipeId = Optional.ofNullable(wednesdayRecipeId);
        this.thursdayRecipeId = Optional.ofNullable(thursdayRecipeId);
        this.fridayRecipeId = Optional.ofNullable(fridayRecipeId);
        this.saturdayRecipeId = Optional.ofNullable(saturdayRecipeId);
        this.sundayRecipeId = Optional.ofNullable(sundayRecipeId);
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
        Optional<Integer> mondayRecipeId,
        Optional<Integer> tuesdayRecipeId,
        Optional<Integer> wednesdayRecipeId,
        Optional<Integer> thursdayRecipeId,
        Optional<Integer> fridayRecipeId,
        Optional<Integer> saturdayRecipeId,
        Optional<Integer> sundayRecipeId
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
                if (mondayRecipeId.isPresent()) {
                    stmt.setInt(1, mondayRecipeId.get());
                } else {
                    stmt.setNull(1, Types.NULL);
                }
                if (tuesdayRecipeId.isPresent()) {
                    stmt.setInt(2, tuesdayRecipeId.get());
                } else {
                    stmt.setNull(2, Types.NULL);
                }
                if (wednesdayRecipeId.isPresent()) {
                    stmt.setInt(3, wednesdayRecipeId.get());
                } else {
                    stmt.setNull(3, Types.NULL);
                }
                if (thursdayRecipeId.isPresent()) {
                    stmt.setInt(4, thursdayRecipeId.get());
                } else {
                    stmt.setNull(4, Types.NULL);
                }
                if (fridayRecipeId.isPresent()) {
                    stmt.setInt(5, fridayRecipeId.get());
                } else {
                    stmt.setNull(5, Types.NULL);
                }
                if (saturdayRecipeId.isPresent()) {
                    stmt.setInt(6, saturdayRecipeId.get());
                } else {
                    stmt.setNull(6, Types.NULL);
                }
                if (sundayRecipeId.isPresent()) {
                    stmt.setInt(7, sundayRecipeId.get());
                } else {
                    stmt.setNull(7, Types.NULL);
                }
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
                if (mondayRecipeId.isPresent()) {
                    stmt.setInt(1, mondayRecipeId.get());
                } else {
                    stmt.setNull(1, Types.NULL);
                }
                if (tuesdayRecipeId.isPresent()) {
                    stmt.setInt(2, tuesdayRecipeId.get());
                } else {
                    stmt.setNull(2, Types.NULL);
                }
                if (wednesdayRecipeId.isPresent()) {
                    stmt.setInt(3, wednesdayRecipeId.get());
                } else {
                    stmt.setNull(3, Types.NULL);
                }
                if (thursdayRecipeId.isPresent()) {
                    stmt.setInt(4, thursdayRecipeId.get());
                } else {
                    stmt.setNull(4, Types.NULL);
                }
                if (fridayRecipeId.isPresent()) {
                    stmt.setInt(5, fridayRecipeId.get());
                } else {
                    stmt.setNull(5, Types.NULL);
                }
                if (saturdayRecipeId.isPresent()) {
                    stmt.setInt(6, saturdayRecipeId.get());
                } else {
                    stmt.setNull(6, Types.NULL);
                }
                if (sundayRecipeId.isPresent()) {
                    stmt.setInt(7, sundayRecipeId.get());
                } else {
                    stmt.setNull(7, Types.NULL);
                }
            }
        );
    }

    public void delete() throws SQLException {
        ModelHelper.delete(id, "WeekMealPlan");
    }
}
