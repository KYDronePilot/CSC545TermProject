package cli;

import java.sql.SQLException;
import java.util.Scanner;
import models.MealPlan;
import picocli.CommandLine.Command;

/**
 * CLI for managing meal plans.
 */
@Command(name = "meals", description = "Meal plan operations")
class MealPlanCli extends ModelCli {

    @Command(name = "add", description = "Add a meal plan")
    int add() {
        try (var scanner = new Scanner(System.in)) {
            var mealPlanName = validatedString(
                "Enter the meal plan name: ",
                maxLengthValidator(20),
                true,
                scanner
            );
            var mealPlanDay = validatedString(
                "Enter the meal plan day of week ('mon', 'tue', 'wed', etc.): ",
                maxLengthValidator(20),
                true,
                scanner
            );
            var newItem = MealPlan.create(mealPlanName.get(), mealPlanDay.get());
        } catch (SQLException e) {}
        return 0;
    }

    @Command(name = "list", description = "List meal plans")
    int list() {
        try {
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
        } catch (SQLException e) {
            return 1;
        }
        return 0;
    }

    @Command(name = "get", description = "Get the details of a meal plan")
    int get() {
        try (var scanner = new Scanner(System.in)) {
            var mealPlanId = validatedInt("Enter the meal plan ID to get: ", null, true, scanner);
            var mealPlan = MealPlan.get(mealPlanId.get());
            if (mealPlan.isEmpty()) {
                System.out.println("ID doesn't exist. Try again.");
                return 1;
            }
            var mealPlanVal = mealPlan.get();
            System.out.printf("\nID: %s\n", mealPlanVal.id);
            System.out.printf("Name: %s\n", mealPlanVal.name);
            System.out.printf("Day: %s\n", mealPlanVal.day);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }

    @Command(name = "update", description = "Update a meal plan's values")
    int update() {
        try (var scanner = new Scanner(System.in)) {
            var mealPlanId = validatedInt(
                "Enter the meal plan ID to update: ",
                null,
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
                maxLengthValidator(20),
                false,
                scanner
            );
            if (mealPlanName.isPresent()) {
                mealPlanVal.name = mealPlanName.get();
            }
            var mealPlanDay = validatedString(
                String.format("Enter the meal plan day (\"%s\"): ", mealPlanVal.day),
                maxLengthValidator(20),
                false,
                scanner
            );
            if (mealPlanDay.isPresent()) {
                mealPlanVal.day = mealPlanDay.get();
            }
            mealPlanVal.update();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }

    @Command(name = "delete", description = "Delete a meal plan")
    int delete() {
        try (var scanner = new Scanner(System.in)) {
            var mealPlanId = validatedInt(
                "Enter the meal plan ID to delete: ",
                null,
                true,
                scanner
            );
            var mealPlan = MealPlan.get(mealPlanId.get());
            if (mealPlan.isEmpty()) {
                System.out.println("ID doesn't exist. Try again.");
                return 1;
            }
            mealPlan.get().delete();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }
}
