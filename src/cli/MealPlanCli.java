package cli;

import database.Database;
import java.sql.SQLException;
import java.util.Scanner;
import models.MealPlan;
import models.Recipe;
import picocli.CommandLine.Command;

/**
 * CLI for managing meal plans.
 */
@Command(name = "meals", description = "Meal plan management", mixinStandardHelpOptions = true)
class MealPlanCli extends ModelCli {

    /**
     * Get a list of all recipes in the system.
     *
     * @return formatted list of recipes
     * @throws SQLException if error executing SQL query
     */
    private String getRecipeList() throws SQLException {
        var recipes = Recipe.filter("select * from Recipe", stmt -> {});
        var text = "Recipes:";
        for (var recipe : recipes) {
            text += String.format("\n  %3d: %s", recipe.id, recipe.name);
        }
        return text;
    }

    /**
     * Delete any existing meals for a meal plan and prompt the user to enter in more.
     *
     * @param scanner active System.in reader
     * @param mealPlanId ID of the meal plan
     * @throws SQLException if error executing SQL query
     */
    private void updateMeals(Scanner scanner, Integer mealPlanId) throws SQLException {
        var db = Database.getInstance();
        // Delete any existing meals
        db.modify(
            "delete from RecipeMealPlan where mealPlanId = ?",
            stmt -> {
                stmt.setInt(1, mealPlanId);
            }
        );
        System.out.print(getRecipeList());
        while (true) {
            // Get meal info
            var meal = validatedString(
                "Enter the name of a meal for this plan (e.g. 'breakfast', 'lunch', etc.): ",
                20,
                true,
                scanner
            );
            var recipeId = validatedPositiveInt(
                "Enter the ID of the recipe for this meal: ",
                true,
                scanner
            );
            // Add to DB
            db.modify(
                "insert into RecipeMealPlan (recipeId, mealPlanId, meal) values (?,?,?)",
                stmt -> {
                    stmt.setInt(1, recipeId.get());
                    stmt.setInt(2, mealPlanId);
                    stmt.setString(3, meal.get());
                }
            );
            // Stop if user is done entering meal plans
            System.out.print("Would you like to add another meal to this plan? (y/N): ");
            if (!scanner.nextLine().toLowerCase().equals("y")) {
                break;
            }
        }
    }

    @Command(name = "add", description = "Add a meal plan")
    int add() {
        return userInteraction(
            scanner -> {
                var mealPlanName = validatedString("Enter the meal plan name: ", 20, true, scanner);
                var mealPlanDay = validatedString(
                    "Enter the meal plan day of week ('mon', 'tue', 'wed', etc.): ",
                    3,
                    true,
                    scanner
                );
                var newItem = MealPlan.create(mealPlanName.get(), mealPlanDay.get());
                updateMeals(scanner, newItem.id);
                return 0;
            }
        );
    }

    @Command(name = "list", description = "List meal plans")
    int list() {
        return userInteraction(
            scanner -> {
                var items = MealPlan.filter("select * from MealPlan", stmt -> {});
                var table = new CliTable(new String[] { "ID", "Name", "Day", "More info..." });
                for (var item : items) {
                    table.rows.add(
                        new String[] {
                            String.valueOf(item.id),
                            item.name,
                            item.day,
                            "Run `get` sub-command for more info",
                        }
                    );
                }
                System.out.println(table.toString());
                return 0;
            }
        );
    }

    @Command(name = "get", description = "Get the details of a meal plan")
    int get() {
        return userInteraction(
            scanner -> {
                var mealPlanId = validatedPositiveInt(
                    "Enter the meal plan ID to get: ",
                    true,
                    scanner
                );
                var mealPlan = MealPlan.get(mealPlanId.get());
                if (mealPlan.isEmpty()) {
                    System.out.println("ID doesn't exist. Try again.");
                    return 1;
                }
                var mealPlanVal = mealPlan.get();
                System.out.printf("\nID: %s\n", mealPlanVal.id);
                System.out.printf("Name: %s\n", mealPlanVal.name);
                System.out.printf("Day: %s\n", mealPlanVal.day);
                System.out.println("Meals");
                var db = Database.getInstance();
                // Get all meals for this meal plan
                db.select(
                    "select rmp.meal as meal, r.name as name from Recipe r join RecipeMealPlan rmp on r.id = rmp.recipeId join MealPlan mp on rmp.mealPlanId = mp.id where mp.id = ?",
                    rs -> {
                        System.out.printf("  %s: %s\n", rs.getString("meal"), rs.getString("name"));
                    },
                    stmt -> {
                        stmt.setInt(1, mealPlanVal.id);
                    }
                );
                return 0;
            }
        );
    }

    @Command(name = "update", description = "Update a meal plan's information")
    int update() {
        return userInteraction(
            scanner -> {
                var mealPlanId = validatedPositiveInt(
                    "Enter the meal plan ID to update: ",
                    true,
                    scanner
                );
                var mealPlan = MealPlan.get(mealPlanId.get());
                if (mealPlan.isEmpty()) {
                    System.out.println("ID doesn't exist. Try again.");
                    return 1;
                }
                var mealPlanVal = mealPlan.get();
                var mealPlanName = validatedString(
                    String.format("Enter the meal plan name (\"%s\"): ", mealPlanVal.name),
                    20,
                    false,
                    scanner
                );
                if (mealPlanName.isPresent()) {
                    mealPlanVal.name = mealPlanName.get();
                }
                var mealPlanDay = validatedString(
                    String.format("Enter the meal plan day (\"%s\"): ", mealPlanVal.day),
                    3,
                    false,
                    scanner
                );
                if (mealPlanDay.isPresent()) {
                    mealPlanVal.day = mealPlanDay.get();
                }
                mealPlanVal.update();
                updateMeals(scanner, mealPlanVal.id);
                return 0;
            }
        );
    }

    @Command(name = "delete", description = "Delete a meal plan")
    int delete() {
        return userInteraction(
            scanner -> {
                var mealPlanId = validatedPositiveInt(
                    "Enter the meal plan ID to delete: ",
                    true,
                    scanner
                );
                var mealPlan = MealPlan.get(mealPlanId.get());
                if (mealPlan.isEmpty()) {
                    System.out.println("ID doesn't exist. Try again.");
                    return 1;
                }
                mealPlan.get().delete();
                return 0;
            }
        );
    }
}
