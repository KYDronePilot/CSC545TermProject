package cli;

import database.Database;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        ArrayList<Recipe> recipes = Recipe.filter("select * from Recipe", stmt -> {});
        String text = "Recipes:";
        for (Recipe recipe : recipes) {
            text += String.format("\n  %3d: %s", recipe.id, recipe.name);
        }
        return text;
    }

    /**
     * Get a list of all recipe IDs in the system.
     *
     * @return recipe ID list
     * @throws SQLException if error executing SQL query
     */
    private List<Integer> getRecipeIdList() throws SQLException {
        ArrayList<Integer> idList = new ArrayList<Integer>();
        Database db = Database.getInstance();
        db.select(
            "select id from recipe",
            rs -> {
                idList.add(rs.getInt("id"));
            }
        );
        return idList;
    }

    /**
     * Delete any existing meals for a meal plan and prompt the user to enter in more.
     *
     * @param scanner active System.in reader
     * @param mealPlanId ID of the meal plan
     * @throws SQLException if error executing SQL query
     */
    private void updateMeals(Scanner scanner, Integer mealPlanId) throws SQLException {
        Database db = Database.getInstance();
        List<Integer> recipeIds = getRecipeIdList();
        // Delete any existing meals
        db.modify(
            "delete from RecipeMealPlan where mealPlanId = ?",
            stmt -> {
                stmt.setInt(1, mealPlanId);
            }
        );
        System.out.println(getRecipeList());
        while (true) {
            // Get meal info
            Optional<String> meal = validatedString(
                "Enter the name of a meal for this plan (e.g. 'breakfast', 'lunch', etc.): ",
                20,
                true,
                scanner
            );
            Optional<Integer> recipeId = validatedPossibleInt(
                "Enter the ID of the recipe for this meal: ",
                recipeIds,
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

    /**
     * Validated input of a string day of week.
     *
     * @param takenDays list of days that already have meal plans
     */
    private Optional<String> validatedDayOfWeek(
        String prompt,
        List<String> takenDays,
        boolean required,
        Scanner scanner
    ) {
        return validatedInput(
            prompt,
            InputValidators.dayOfWeekValidator(takenDays),
            value -> {
                return value;
            },
            required,
            scanner,
            readerScanner -> {
                return readerScanner.nextLine();
            }
        );
    }

    /**
     * Get a list of days of the week that already have a meal plan.
     *
     * @return days of the week that already have a meal plan
     */
    private List<String> getTakenDays() throws SQLException {
        ArrayList<String> takenDays = new ArrayList<String>();
        Database db = Database.getInstance();
        db.select(
            "select day from MealPlan",
            rs -> {
                takenDays.add(rs.getString("day"));
            }
        );
        return takenDays;
    }

    @Command(name = "add", description = "Add a meal plan")
    @Override
    int add() {
        return userInteraction(
            scanner -> {
                List<String> takenDays = getTakenDays();
                Optional<String> mealPlanName = validatedString(
                    "Enter the meal plan name: ",
                    20,
                    true,
                    scanner
                );
                Optional<String> mealPlanDay = validatedDayOfWeek(
                    "Enter the meal plan day of week ('mon', 'tue', 'wed', etc.): ",
                    takenDays,
                    true,
                    scanner
                );
                MealPlan newItem = MealPlan.create(mealPlanName.get(), mealPlanDay.get());
                updateMeals(scanner, newItem.id);
                return 0;
            }
        );
    }

    @Command(name = "list", description = "List meal plans")
    @Override
    int list() {
        return userInteraction(
            scanner -> {
                ArrayList<MealPlan> items = MealPlan.filter("select * from MealPlan", stmt -> {});
                CliTable table = new CliTable(new String[] { "ID", "Name", "Day", "More info..." });
                for (MealPlan item : items) {
                    table.rows.add(
                        new String[] {
                            String.valueOf(item.id),
                            item.name,
                            item.day,
                            "Run `meals get` for more info",
                        }
                    );
                }
                System.out.println(table.toString());
                return 0;
            }
        );
    }

    @Command(name = "get", description = "Get the details of a meal plan")
    @Override
    int get() {
        return userInteraction(
            scanner -> {
                Optional<Integer> mealPlanId = validatedPositiveInt(
                    "Enter the meal plan ID to get: ",
                    true,
                    scanner
                );
                Optional<MealPlan> mealPlan = MealPlan.get(mealPlanId.get());
                if (!mealPlan.isPresent()) {
                    System.out.println("ID doesn't exist. Try again.");
                    return 1;
                }
                MealPlan mealPlanVal = mealPlan.get();
                System.out.printf("\nID: %s\n", mealPlanVal.id);
                System.out.printf("Name: %s\n", mealPlanVal.name);
                System.out.printf("Day: %s\n", mealPlanVal.day);
                System.out.println("Meals");
                Database db = Database.getInstance();
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
    @Override
    int update() {
        return userInteraction(
            scanner -> {
                Optional<Integer> mealPlanId = validatedPositiveInt(
                    "Enter the meal plan ID to update: ",
                    true,
                    scanner
                );
                Optional<MealPlan> mealPlan = MealPlan.get(mealPlanId.get());
                if (!mealPlan.isPresent()) {
                    System.out.println("ID doesn't exist. Try again.");
                    return 1;
                }
                MealPlan mealPlanVal = mealPlan.get();
                List<String> takenDays = getTakenDays();
                Optional<String> mealPlanName = validatedString(
                    String.format("Enter the meal plan name (\"%s\"): ", mealPlanVal.name),
                    20,
                    false,
                    scanner
                );
                if (mealPlanName.isPresent()) {
                    mealPlanVal.name = mealPlanName.get();
                }
                Optional<String> mealPlanDay = validatedDayOfWeek(
                    String.format("Enter the meal plan day of week (\"%s\"): ", mealPlanVal.day),
                    takenDays,
                    false,
                    scanner
                );
                if (mealPlanDay.isPresent()) {
                    mealPlanVal.day = mealPlanDay.get();
                }
                mealPlanVal.update();
                System.out.println(
                    "Please enter the meals for this meal plan (old ones have been deleted)"
                );
                updateMeals(scanner, mealPlanVal.id);
                return 0;
            }
        );
    }

    @Command(name = "delete", description = "Delete a meal plan")
    @Override
    int delete() {
        return userInteraction(
            scanner -> {
                Optional<Integer> mealPlanId = validatedPositiveInt(
                    "Enter the meal plan ID to delete: ",
                    true,
                    scanner
                );
                Optional<MealPlan> mealPlan = MealPlan.get(mealPlanId.get());
                if (!mealPlan.isPresent()) {
                    System.out.println("ID doesn't exist. Try again.");
                    return 1;
                }
                mealPlan.get().delete();
                return 0;
            }
        );
    }
}
