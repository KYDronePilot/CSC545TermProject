package cli;

import java.util.ArrayList;
import java.util.Optional;
import models.FoodItem;
import models.NutritionFacts;
import picocli.CommandLine.Command;

/**
 * CLI for managing food items and their nutrition facts.
 */
@Command(name = "food", description = "Food item management", mixinStandardHelpOptions = true)
class FoodItemCli extends ModelCli {

    @Command(name = "add", description = "Add a food item")
    int add() {
        return userInteraction(
            scanner -> {
                Optional<String> name = validatedString("Enter the food name: ", 50, true, scanner);
                Optional<String> group = validatedString(
                    "Enter the food group: ",
                    30,
                    true,
                    scanner
                );
                Optional<Integer> units = validatedPositiveInt(
                    "Enter the number of units you currently have (default: 0): ",
                    false,
                    scanner
                );
                Optional<Integer> calories = validatedPositiveInt(
                    "Enter the number of calories: ",
                    true,
                    scanner
                );
                Optional<Integer> sugar = validatedPositiveInt(
                    "Enter the number of sugar: ",
                    true,
                    scanner
                );
                Optional<Integer> protein = validatedPositiveInt(
                    "Enter the number of protein: ",
                    true,
                    scanner
                );
                Optional<Integer> sodium = validatedPositiveInt(
                    "Enter the number of sodium: ",
                    true,
                    scanner
                );
                Optional<Integer> fat = validatedPositiveInt(
                    "Enter the number of fat: ",
                    true,
                    scanner
                );
                NutritionFacts newNutritionFacts = NutritionFacts.create(
                    calories.get(),
                    sugar.get(),
                    protein.get(),
                    sodium.get(),
                    fat.get()
                );
                FoodItem.create(
                    name.get(),
                    newNutritionFacts.id,
                    group.get(),
                    units.isPresent() ? units.get() : 0
                );
                return 0;
            }
        );
    }

    @Command(name = "list", description = "List food items")
    int list() {
        return userInteraction(
            scanner -> {
                ArrayList<FoodItem> items = FoodItem.filter("select * from FoodItem", stmt -> {});
                CliTable table = new CliTable(
                    new String[] { "ID", "Name", "Food Group", "Units", "More info..." }
                );
                for (FoodItem item : items) {
                    table.rows.add(
                        new String[] {
                            String.valueOf(item.id),
                            item.name,
                            item.foodGroup,
                            String.valueOf(item.units),
                            "Run `food get` for more info",
                        }
                    );
                }
                System.out.println(table);
                return 0;
            }
        );
    }

    @Command(name = "get", description = "Get the details of a food item")
    int get() {
        return userInteraction(
            scanner -> {
                Optional<Integer> foodId = validatedPositiveInt(
                    "Enter the food ID to get: ",
                    true,
                    scanner
                );
                Optional<FoodItem> foodItem = FoodItem.get(foodId.get());
                if (!foodItem.isPresent()) {
                    System.out.println("ID doesn't exist. Try again.");
                    return 1;
                }
                FoodItem foodItemVal = foodItem.get();
                NutritionFacts nutritionFacts = foodItemVal.getNutritionFacts();
                System.out.printf("\nID: %s\n", foodItemVal.id);
                System.out.printf("Name: %s\n", foodItemVal.name);
                System.out.printf("Food Group: %s\n", foodItemVal.foodGroup);
                System.out.printf("Units: %s\n", foodItemVal.units);
                System.out.printf("Calories: %s\n", nutritionFacts.calories);
                System.out.printf("Sugar: %s\n", nutritionFacts.sugar);
                System.out.printf("Protein: %s\n", nutritionFacts.protein);
                System.out.printf("Sodium: %s\n", nutritionFacts.sodium);
                System.out.printf("Fat: %s\n", nutritionFacts.fat);
                return 0;
            }
        );
    }

    @Command(name = "update", description = "Update a food item's information")
    int update() {
        return userInteraction(
            scanner -> {
                Optional<Integer> foodId = validatedPositiveInt(
                    "Enter the food ID to update: ",
                    true,
                    scanner
                );
                Optional<FoodItem> foodItem = FoodItem.get(foodId.get());
                if (!foodItem.isPresent()) {
                    System.out.println("ID doesn't exist. Try again.");
                    return 1;
                }
                FoodItem foodItemVal = foodItem.get();
                NutritionFacts nutritionFacts = foodItemVal.getNutritionFacts();
                Optional<String> foodName = validatedString(
                    String.format("Enter the food name (\"%s\"): ", foodItemVal.name),
                    50,
                    false,
                    scanner
                );
                if (foodName.isPresent()) {
                    foodItemVal.name = foodName.get();
                }
                Optional<String> foodGroup = validatedString(
                    String.format("Enter the food group (\"%s\"): ", foodItemVal.foodGroup),
                    30,
                    false,
                    scanner
                );
                if (foodGroup.isPresent()) {
                    foodItemVal.foodGroup = foodGroup.get();
                }
                Optional<Integer> units = validatedPositiveInt(
                    String.format("Enter the food units (\"%s\"): ", foodItemVal.units),
                    false,
                    scanner
                );
                if (units.isPresent()) {
                    foodItemVal.units = units.get();
                }

                Optional<Integer> calories = validatedPositiveInt(
                    String.format("Enter the number of calories (%s): ", nutritionFacts.calories),
                    false,
                    scanner
                );
                if (calories.isPresent()) {
                    nutritionFacts.calories = calories.get();
                }
                Optional<Integer> sugar = validatedPositiveInt(
                    String.format("Enter the number of sugar (%s): ", nutritionFacts.sugar),
                    false,
                    scanner
                );
                if (sugar.isPresent()) {
                    nutritionFacts.sugar = sugar.get();
                }
                Optional<Integer> protein = validatedPositiveInt(
                    String.format("Enter the number of protein (%s): ", nutritionFacts.protein),
                    false,
                    scanner
                );
                if (protein.isPresent()) {
                    nutritionFacts.protein = protein.get();
                }
                Optional<Integer> sodium = validatedPositiveInt(
                    String.format("Enter the number of sodium (%s): ", nutritionFacts.sodium),
                    false,
                    scanner
                );
                if (sodium.isPresent()) {
                    nutritionFacts.sodium = sodium.get();
                }
                Optional<Integer> fat = validatedPositiveInt(
                    String.format("Enter the number of fat (%s): ", nutritionFacts.fat),
                    false,
                    scanner
                );
                if (fat.isPresent()) {
                    nutritionFacts.fat = fat.get();
                }
                foodItemVal.update();
                nutritionFacts.update();
                return 0;
            }
        );
    }

    @Command(name = "delete", description = "Delete a food item")
    int delete() {
        return userInteraction(
            scanner -> {
                Optional<Integer> foodId = validatedPositiveInt(
                    "Enter the food ID to delete: ",
                    true,
                    scanner
                );
                Optional<FoodItem> foodItem = FoodItem.get(foodId.get());
                if (!foodItem.isPresent()) {
                    System.out.println("ID doesn't exist. Try again.");
                    return 1;
                }
                NutritionFacts nutritionFacts = foodItem.get().getNutritionFacts();
                foodItem.get().delete();
                nutritionFacts.delete();
                return 0;
            }
        );
    }
}
