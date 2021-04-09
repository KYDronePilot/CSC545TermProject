package models;

import database.Database;
import database.ThrowingConsumer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Optional;
import utils.ModelHelper;

public class Recipe {

    public int id;
    public String name;
    public Optional<String> instructions;
    public Optional<String> category;

    public Recipe(int id, String name, String instructions, String category) {
        this.id = id;
        this.name = name;
        this.instructions = Optional.ofNullable(instructions);
        this.category = Optional.ofNullable(category);
    }

    public static Recipe get(Integer id) throws SQLException {
        return ModelHelper.get(
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

    public static Recipe create(
        String name,
        Optional<String> instructions,
        Optional<String> category
    )
        throws SQLException {
        var db = Database.getInstance();
        var id = db.insert(
            "Recipe",
            new String[] { "name", "instructions", "category" },
            stmt -> {
                stmt.setString(1, name);
                if (instructions.isPresent()) {
                    stmt.setString(2, instructions.get());
                } else {
                    stmt.setNull(2, Types.NULL);
                }
                if (category.isPresent()) {
                    stmt.setString(3, category.get());
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
            "Recipe",
            new String[] { "name", "instructions", "category" },
            id,
            stmt -> {
                stmt.setString(1, name);
                if (instructions.isPresent()) {
                    stmt.setString(2, instructions.get());
                } else {
                    stmt.setNull(2, Types.NULL);
                }
                if (category.isPresent()) {
                    stmt.setString(3, category.get());
                } else {
                    stmt.setNull(3, Types.NULL);
                }
            }
        );
    }

    public void delete() throws SQLException {
        ModelHelper.delete(id, "Recipe");
    }
}
