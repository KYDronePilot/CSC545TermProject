package cli;

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
                var name = validatedString("Enter the food name: ", 50, true, scanner);
                var group = validatedString("Enter the food group: ", 30, true, scanner);
                var units = validatedPositiveInt(
                    "Enter the number of units you currently have (default: 0): ",
                    false,
                    scanner
                );
                var calories = validatedPositiveInt(
                    "Enter the number of calories: ",
                    true,
                    scanner
                );
                var sugar = validatedPositiveInt("Enter the number of sugar: ", true, scanner);
                var protein = validatedPositiveInt("Enter the number of protein: ", true, scanner);
                var sodium = validatedPositiveInt("Enter the number of sodium: ", true, scanner);
                var fat = validatedPositiveInt("Enter the number of fat: ", true, scanner);
                var newNutritionFacts = NutritionFacts.create(
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
                var items = FoodItem.filter("select * from FoodItem", stmt -> {});
                var table = new CliTable(
                    new String[] { "ID", "Name", "Food Group", "Units", "More info..." }
                );
                for (var item : items) {
                    table.rows.add(
                        new String[] {
                            String.valueOf(item.id),
                            item.name,
                            item.foodGroup,
                            String.valueOf(item.units),
                            "Run `get` sub-command for more info",
                        }
                    );
                }
                System.out.println(table.toString());
                return 0;
            }
        );
    }

    @Command(name = "get", description = "Get the details of a food item")
    int get() {
        return userInteraction(
            scanner -> {
                var foodId = validatedPositiveInt("Enter the food ID to get: ", true, scanner);
                var foodItem = FoodItem.get(foodId.get());
                if (foodItem.isEmpty()) {
                    System.out.println("ID doesn't exist. Try again.");
                    return 1;
                }
                var foodItemVal = foodItem.get();
                var nutritionFacts = foodItemVal.getNutritionFacts();
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
                var foodId = validatedPositiveInt("Enter the food ID to update: ", true, scanner);
                var foodItem = FoodItem.get(foodId.get());
                if (foodItem.isEmpty()) {
                    System.out.println("ID doesn't exist. Try again.");
                    return 1;
                }
                var foodItemVal = foodItem.get();
                var nutritionFacts = foodItemVal.getNutritionFacts();
                var foodName = validatedString(
                    String.format("Enter the food name (\"%s\"): ", foodItemVal.name),
                    50,
                    false,
                    scanner
                );
                if (foodName.isPresent()) {
                    foodItemVal.name = foodName.get();
                }
                var foodGroup = validatedString(
                    String.format("Enter the food group (\"%s\"): ", foodItemVal.foodGroup),
                    30,
                    false,
                    scanner
                );
                if (foodGroup.isPresent()) {
                    foodItemVal.foodGroup = foodGroup.get();
                }
                var units = validatedPositiveInt(
                    String.format("Enter the food units (\"%s\"): ", foodItemVal.units),
                    false,
                    scanner
                );
                if (units.isPresent()) {
                    foodItemVal.units = units.get();
                }

                var calories = validatedPositiveInt(
                    String.format("Enter the number of calories (%s): ", nutritionFacts.calories),
                    false,
                    scanner
                );
                if (calories.isPresent()) {
                    nutritionFacts.calories = calories.get();
                }
                var sugar = validatedPositiveInt(
                    String.format("Enter the number of sugar (%s): ", nutritionFacts.sugar),
                    false,
                    scanner
                );
                if (sugar.isPresent()) {
                    nutritionFacts.sugar = sugar.get();
                }
                var protein = validatedPositiveInt(
                    String.format("Enter the number of protein (%s): ", nutritionFacts.protein),
                    false,
                    scanner
                );
                if (protein.isPresent()) {
                    nutritionFacts.protein = protein.get();
                }
                var sodium = validatedPositiveInt(
                    String.format("Enter the number of sodium (%s): ", nutritionFacts.sodium),
                    false,
                    scanner
                );
                if (sodium.isPresent()) {
                    nutritionFacts.sodium = sodium.get();
                }
                var fat = validatedPositiveInt(
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
                var foodId = validatedPositiveInt("Enter the food ID to delete: ", true, scanner);
                var foodItem = FoodItem.get(foodId.get());
                if (foodItem.isEmpty()) {
                    System.out.println("ID doesn't exist. Try again.");
                    return 1;
                }
                foodItem.get().delete();
                return 0;
            }
        );
    }
}
