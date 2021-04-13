package models;

import database.Database;
import database.ThrowingConsumer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import utils.ModelHelper;

public class FoodItem {

    // Names of columns (excluding id)
    public static String[] columns = { "name", "nutritionFactsId", "foodGroup" };

    public int id;
    public String name;
    public Integer nutritionFactsId;
    public String foodGroup;

    public FoodItem(int id, String name, Integer nutritionFactsId, String foodGroup) {
        this.id = id;
        this.name = name;
        this.nutritionFactsId = nutritionFactsId;
        this.foodGroup = foodGroup;
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
                        rs.getString("foodGroup")
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
                    rs.getString("foodGroup")
                );
            }
        );
    }

    public static FoodItem create(String name, Integer nutritionFactsId, String foodGroup)
        throws SQLException {
        var db = Database.getInstance();
        var id = db.insert(
            "FoodItem",
            columns,
            stmt -> {
                stmt.setString(1, name);
                stmt.setInt(2, nutritionFactsId);
                stmt.setString(3, foodGroup);
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
}
