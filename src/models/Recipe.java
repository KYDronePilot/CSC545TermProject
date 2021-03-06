package models;

import database.Database;
import database.ThrowingConsumer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import utils.ModelHelper;

public class Recipe implements Comparable<Recipe> {

    public Integer id;
    public String name;
    public String instructions;
    public String category;

    public Recipe(int id, String name, String instructions, String category) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.category = category;
    }

    public static Optional<Recipe> get(Integer id) throws SQLException {
        return Optional.ofNullable(
            ModelHelper.get(
                id,
                "Recipe",
                rs -> {
                    return new Recipe(
                        id,
                        rs.getString("name"),
                        rs.getString("instructions"),
                        rs.getString("category")
                    );
                }
            )
        );
    }

    public static ArrayList<Recipe> filter(
        String sql,
        ThrowingConsumer<PreparedStatement, SQLException> setValues
    )
        throws SQLException {
        return ModelHelper.filter(
            sql,
            setValues,
            rs -> {
                return new Recipe(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("instructions"),
                    rs.getString("category")
                );
            }
        );
    }

    public static Recipe create(String name, String instructions, String category)
        throws SQLException {
        Database db = Database.getInstance();
        Optional<Integer> id = db.insert(
            "Recipe",
            new String[] { "name", "instructions", "category" },
            stmt -> {
                stmt.setString(1, name);
                stmt.setString(2, instructions);
                stmt.setString(3, category);
            },
            true
        );
        return get(id.get()).get();
    }

    public void update() throws SQLException {
        Database db = Database.getInstance();
        db.update(
            "Recipe",
            new String[] { "name", "instructions", "category" },
            id,
            stmt -> {
                stmt.setString(1, name);
                stmt.setString(2, instructions);
                stmt.setString(3, category);
            }
        );
    }

    public void delete() throws SQLException {
        ModelHelper.delete(id, "Recipe");
    }

    public List<FoodItem> getFoodItems() throws SQLException {
        return FoodItem.filter(
            "select fi.* from FoodItem fi join RecipeFoodItem rfi on fi.id = rfi.foodItemId join Recipe r on rfi.recipeId = r.id where r.id = ?",
            stmt -> {
                stmt.setInt(1, id);
            }
        );
    }

    @Override
    public int compareTo(Recipe that) {
        return this.id.compareTo(that.id);
    }
}
