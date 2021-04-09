package models;

import database.Database;
import database.ThrowingConsumer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Optional;
import utils.ModelHelper;

public class FoodItem {

    public int id;
    public String name;
    public Optional<Integer> nutritionFactsId;
    public Optional<String> foodGroup;

    public FoodItem(int id, String name, int nutritionFactsId, String foodGroup) {
        this.id = id;
        this.name = name;
        this.nutritionFactsId = Optional.ofNullable(nutritionFactsId);
        this.foodGroup = Optional.ofNullable(foodGroup);
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

    public static FoodItem create(
        String name,
        Optional<Integer> nutritionFactsId,
        Optional<String> foodGroup
    )
        throws SQLException {
        var db = Database.getInstance();
        var id = db.insert(
            "FoodItem",
            new String[] { "name", "nutritionFactsId", "foodGroup" },
            stmt -> {
                stmt.setString(1, name);
                if (nutritionFactsId.isPresent()) {
                    stmt.setInt(2, nutritionFactsId.get());
                } else {
                    stmt.setNull(2, Types.NULL);
                }
                if (foodGroup.isPresent()) {
                    stmt.setString(3, foodGroup.get());
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
            "FoodItem",
            new String[] { "name", "nutritionFactsId", "foodGroup" },
            id,
            stmt -> {
                stmt.setString(1, name);
                if (nutritionFactsId.isPresent()) {
                    stmt.setInt(2, nutritionFactsId.get());
                } else {
                    stmt.setNull(2, Types.NULL);
                }
                if (foodGroup.isPresent()) {
                    stmt.setString(3, foodGroup.get());
                } else {
                    stmt.setNull(3, Types.NULL);
                }
            }
        );
    }

    public void delete() throws SQLException {
        ModelHelper.delete(id, "FoodItem");
    }
}
