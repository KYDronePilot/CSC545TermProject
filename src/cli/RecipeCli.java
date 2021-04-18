package cli;

import database.Database;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Scanner;
import models.FoodItem;
import models.Recipe;
import picocli.CommandLine.Command;

/**
 * CLI for managing recipes.
 */
@Command(name = "recipe", description = "Recipe operations")
class RecipeCli extends ModelCli {

    /**
     * Link a list of ingredients to a particular recipe.
     *
     * @param recipeId to link ingredients to
     * @param rawIngredients comma-separated list of ingredient IDs
     * @throws SQLException if error executing query
     */
    private void saveIngredients(Integer recipeId, String rawIngredients) throws SQLException {
        var db = Database.getInstance();
        // Delete any existing ingredients for recipe
        db.modify(
            "delete from RecipeFoodItem where recipeId = ?",
            stmt -> {
                stmt.setInt(1, recipeId);
            }
        );
        // Create new M2M ingredient links for recipe
        var splitIngredients = rawIngredients.split(",");
        for (var ingredient : splitIngredients) {
            var ingredientId = Integer.parseInt(ingredient.strip());
            db.modify(
                "insert into RecipeFoodItem (recipeId, foodItemId) values (?, ?)",
                stmt -> {
                    stmt.setInt(1, recipeId);
                    stmt.setInt(2, ingredientId);
                }
            );
        }
    }

    @Command(name = "add", description = "Add a Recipe")
    int add() {
        try (var scanner = new Scanner(System.in)) {
            var ingredients = FoodItem.filter("select * from FoodItem", stmt -> {});
            Collections.sort(ingredients);
            var recipeName = validatedString(
                "Enter recipe name: ",
                maxLengthValidator(20),
                true,
                scanner
            );
            var recipeCategory = validatedString(
                "Enter the recipe category: ",
                maxLengthValidator(20),
                true,
                scanner
            );
            System.out.println("The following are all available recipe ingredients:");
            for (var ingredient : ingredients) {
                System.out.printf("  %3d: %s\n", ingredient.id, ingredient.name);
            }
            var recipeIngredientIds = validatedString(
                "Enter comma-separated IDs of ingredients used in recipe: ",
                null,
                true,
                scanner
            );
            var recipeInstructions = validatedMultilineString(
                "Enter recipe instructions (type \"/<return>\" on an empty line to denote the end):\n",
                true,
                scanner
            );
            System.out.println("Saving to DB...");
            var newItem = Recipe.create(
                recipeName.get(),
                recipeInstructions.get(),
                recipeCategory.get()
            );
            saveIngredients(newItem.id, recipeIngredientIds.get());
        } catch (SQLException e) {}
        return 0;
    }

    @Command(name = "list", description = "List recipes")
    int list() {
        try {
            var items = Recipe.filter("select * from Recipe", stmt -> {});
            var table = new CliTable(new String[] { "ID", "Name", "Category", "More info..." });
            for (var item : items) {
                table.rows.add(
                    new String[] {
                        String.valueOf(item.id),
                        item.name,
                        item.category,
                        "Run `get` sub-command for more info",
                    }
                );
            }
            System.out.println(table.toString());
        } catch (SQLException e) {
            return 1;
        }
        return 0;
    }

    @Command(name = "get", description = "Get the details of a recipe")
    int get() {
        try (var scanner = new Scanner(System.in)) {
            var foodId = validatedInt("Enter the recipe ID to get: ", null, true, scanner);
            var recipe = Recipe.get(foodId.get());
            if (recipe.isEmpty()) {
                System.out.println("ID doesn't exist. Try again.");
                return 1;
            }
            var recipeVal = recipe.get();
            var ingredients = recipeVal.getFoodItems();
            System.out.printf("\nID: %s\n", recipeVal.id);
            System.out.printf("Name: %s\n", recipeVal.name);
            System.out.printf("Category: %s\n", recipeVal.category);
            System.out.println("Ingredients:");
            for (var ingredient : ingredients) {
                System.out.printf("  %s\n", ingredient);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }

    @Command(name = "update", description = "Update a recipe's values")
    int update() {
        try (var scanner = new Scanner(System.in)) {
            var ingredients = FoodItem.filter("select * from FoodItem", stmt -> {});
            Collections.sort(ingredients);
            var recipeId = validatedInt("Enter the recipe ID to update: ", null, true, scanner);
            var recipe = Recipe.get(recipeId.get());
            if (recipe.isEmpty()) {
                System.out.println("ID doesn't exist. Try again.");
                return 1;
            }
            var recipeVal = recipe.get();
            var recipeName = validatedString(
                String.format("Enter the recipe name (\"%s\"): ", recipeVal.name),
                maxLengthValidator(20),
                false,
                scanner
            );
            if (recipeName.isPresent()) {
                recipeVal.name = recipeName.get();
            }
            var recipeCategory = validatedString(
                String.format("Enter the recipe category (\"%s\"): ", recipeVal.category),
                maxLengthValidator(20),
                false,
                scanner
            );
            System.out.println("The following are all available recipe ingredients:");
            for (var ingredient : ingredients) {
                System.out.printf("  %3d: %s\n", ingredient.id, ingredient.name);
            }
            var recipeIngredientIds = validatedString(
                "Enter comma-separated IDs of ingredients used in recipe: ",
                null,
                true,
                scanner
            );
            if (recipeCategory.isPresent()) {
                recipeVal.category = recipeCategory.get();
            }
            var recipeInstructions = validatedMultilineString(
                "Enter the recipe instructions (\"...\"):\n",
                false,
                scanner
            );
            if (recipeInstructions.isPresent()) {
                recipeVal.instructions = recipeInstructions.get();
            }
            System.out.println("Saving to DB...");
            recipeVal.update();
            saveIngredients(recipeVal.id, recipeIngredientIds.get());
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }

    @Command(name = "delete", description = "Delete a recipe")
    int delete() {
        try (var scanner = new Scanner(System.in)) {
            var recipeId = validatedInt("Enter the recipe ID to delete: ", null, true, scanner);
            var recipe = FoodItem.get(recipeId.get());
            if (recipe.isEmpty()) {
                System.out.println("ID doesn't exist. Try again.");
                return 1;
            }
            recipe.get().delete();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }
}