package models;

import database.Database;
import database.ThrowingConsumer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import utils.ModelHelper;

public class FoodItem {

    public int id;
    public String name;
    public int nutritionFactsId;
    public String foodGroup;

    public FoodItem(int id, String name, int nutritionFactsId, String foodGroup) {
        this.id = id;
        this.name = name;
        this.nutritionFactsId = nutritionFactsId;
        this.foodGroup = foodGroup;
    }

    public static FoodItem get(Integer id) throws SQLException {
        return ModelHelper.get(
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

    public static FoodItem create(String name, int nutritionFactsId, String foodGroup)
        throws SQLException {
        var db = Database.getInstance();
        var id = db.insert(
            "FoodItem",
            new String[] { "name", "nutritionFactsId", "foodGroup" },
            stmt -> {
                stmt.setString(1, name);
                stmt.setInt(1, nutritionFactsId);
                stmt.setString(1, foodGroup);
            },
            true
        );
        return get(id.get());
    }

    public void update() throws SQLException {
        var db = Database.getInstance();
        db.update(
            "FoodItem",
            new String[] { "name", "nutritionFactsId", "foodGroup" },
            id,
            stmt -> {
                stmt.setString(1, name);
                stmt.setInt(1, nutritionFactsId);
                stmt.setString(1, foodGroup);
            }
        );
    }

    public void delete() throws SQLException {
        ModelHelper.delete(id, "FoodItem");
    }
}
