package models;

import database.Database;
import database.ThrowingConsumer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import utils.ModelHelper;

public class FoodItem implements Comparable<FoodItem> {

    // Names of columns (excluding id)
    public static String[] columns = { "name", "nutritionFactsId", "foodGroup" };

    public Integer id;
    public String name;
    public Integer nutritionFactsId;
    public String foodGroup;
    public int units;

    public FoodItem(int id, String name, Integer nutritionFactsId, String foodGroup, int units) {
        this.id = id;
        this.name = name;
        this.nutritionFactsId = nutritionFactsId;
        this.foodGroup = foodGroup;
        this.units = units;
    }

    public static Optional<FoodItem> get(Integer id) throws SQLException {
        return Optional.ofNullable(
            ModelHelper.get(
                id,
                "FoodItem",
                rs -> {
                    return new FoodItem(
                        id,
                        rs.getString("name"),
                        rs.getInt("nutritionFactsId"),
                        rs.getString("foodGroup"),
                        rs.getInt("units")
                    );
                }
            )
        );
    }

    public static ArrayList<FoodItem> filter(
        String sql,
        ThrowingConsumer<PreparedStatement, SQLException> setValues
    )
        throws SQLException {
        return ModelHelper.filter(
            sql,
            setValues,
            rs -> {
                return new FoodItem(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("nutritionFactsId"),
                    rs.getString("foodGroup"),
                    rs.getInt("units")
                );
            }
        );
    }

    public static FoodItem create(
        String name,
        Integer nutritionFactsId,
        String foodGroup,
        int units
    )
        throws SQLException {
        var db = Database.getInstance();
        var id = db.insert(
            "FoodItem",
            columns,
            stmt -> {
                stmt.setString(1, name);
                stmt.setInt(2, nutritionFactsId);
                stmt.setString(3, foodGroup);
                stmt.setInt(4, units);
            },
            true
        );
        return get(id.get()).get();
    }

    public void update() throws SQLException {
        var db = Database.getInstance();
        db.update(
            "FoodItem",
            columns,
            id,
            stmt -> {
                stmt.setString(1, name);
                stmt.setInt(2, nutritionFactsId);
                stmt.setString(3, foodGroup);
                stmt.setInt(4, units);
            }
        );
    }

    public void delete() throws SQLException {
        ModelHelper.delete(id, "FoodItem");
    }

    /**
     * Getter for related nutrition facts.
     *
     * @return related nutrition facts instance
     * @throws SQLException if error executing SQL
     */
    public NutritionFacts getNutritionFacts() throws SQLException {
        return NutritionFacts.get(nutritionFactsId);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, foodGroup);
    }

    @Override
    public int compareTo(FoodItem that) {
        return this.id.compareTo(that.id);
    }
}
