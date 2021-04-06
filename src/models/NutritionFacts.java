package models;

import database.Database;
import database.ThrowingConsumer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import utils.ModelHelper;

public class NutritionFacts {

    public int id;
    public int calories;
    public int sugar;
    public int protein;
    public int sodium;
    public int fat;

    public NutritionFacts(int id, int calories, int sugar, int protein, int sodium, int fat) {
        this.id = id;
        this.calories = calories;
        this.sugar = sugar;
        this.protein = protein;
        this.sodium = sodium;
        this.fat = fat;
    }

    public static NutritionFacts get(Integer id) throws SQLException {
        return ModelHelper.get(
            id,
            "NutritionFacts",
            rs -> {
                return new NutritionFacts(
                    id,
                    rs.getInt("calories"),
                    rs.getInt("sugar"),
                    rs.getInt("protein"),
                    rs.getInt("sodium"),
                    rs.getInt("fat")
                );
            }
        );
    }

    public static ArrayList<NutritionFacts> filter(
        String sql,
        ThrowingConsumer<PreparedStatement, SQLException> setValues
    )
        throws SQLException {
        return ModelHelper.filter(
            sql,
            setValues,
            rs -> {
                return new NutritionFacts(
                    rs.getInt("id"),
                    rs.getInt("calories"),
                    rs.getInt("sugar"),
                    rs.getInt("protein"),
                    rs.getInt("sodium"),
                    rs.getInt("fat")
                );
            }
        );
    }

    public static NutritionFacts create(int calories, int sugar, int protein, int sodium, int fat)
        throws SQLException {
        var db = Database.getInstance();
        var id = db.insert(
            "NutritionFacts",
            new String[] { "calories", "sugar", "protein", "sodium", "fat" },
            stmt -> {
                stmt.setInt(1, calories);
                stmt.setInt(2, sugar);
                stmt.setInt(3, protein);
                stmt.setInt(4, sodium);
                stmt.setInt(5, fat);
            },
            true
        );
        return get(id.get());
    }

    public void update() throws SQLException {
        var db = Database.getInstance();
        db.update(
            "NutritionFacts",
            new String[] { "calories", "sugar", "protein", "sodium", "fat" },
            id,
            stmt -> {
                stmt.setInt(1, calories);
                stmt.setInt(2, sugar);
                stmt.setInt(3, protein);
                stmt.setInt(4, sodium);
                stmt.setInt(5, fat);
            }
        );
    }
}
