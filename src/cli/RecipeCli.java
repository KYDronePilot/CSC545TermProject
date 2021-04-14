package cli;

import java.sql.SQLException;
import java.util.Scanner;
import models.FoodItem;
import models.Recipe;
import picocli.CommandLine.Command;

/**
 * CLI for managing recipes.
 */
@Command(name = "recipe", description = "Recipe operations")
class RecipeCli extends ModelCli {

    @Command(name = "add", description = "Add a Recipe")
    int add() {
        try (var scanner = new Scanner(System.in)) {
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
            System.out.printf("\nID: %s\n", recipeVal.id);
            System.out.printf("Name: %s\n", recipeVal.name);
            System.out.printf("Category: %s\n", recipeVal.category);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }

    @Command(name = "update", description = "Update a recipe's values")
    int update() {
        try (var scanner = new Scanner(System.in)) {
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
