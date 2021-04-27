package cli;

import database.Database;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import models.FoodItem;
import models.Recipe;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * CLI for managing recipes.
 */
@Command(name = "recipe", description = "Recipe management", mixinStandardHelpOptions = true)
class RecipeCli extends ModelCli {

    /**
     * Link a list of ingredients to a particular recipe.
     *
     * @param recipeId to link ingredients to
     * @param ingredientIds list of ingredient IDs
     * @throws SQLException if error executing query
     */
    private void saveIngredients(Integer recipeId, List<Integer> ingredientIds)
        throws SQLException {
        Database db = Database.getInstance();
        // Delete any existing ingredients for recipe
        db.modify(
            "delete from RecipeFoodItem where recipeId = ?",
            stmt -> {
                stmt.setInt(1, recipeId);
            }
        );
        // Create new M2M ingredient links for recipe
        for (Integer ingredientId : ingredientIds) {
            db.modify(
                "insert into RecipeFoodItem (recipeId, foodItemId) values (?, ?)",
                stmt -> {
                    stmt.setInt(1, recipeId);
                    stmt.setInt(2, ingredientId);
                }
            );
        }
    }

    private List<Integer> getIngredientIds() throws SQLException {
        ArrayList<Integer> idList = new ArrayList<>();
        Database db = Database.getInstance();
        db.select(
            "select id from FoodItem",
            rs -> {
                idList.add(rs.getInt("id"));
            }
        );
        return idList;
    }

    private String getRecipeTable(List<Recipe> recipes) {
        CliTable table = new CliTable(new String[] { "ID", "Name", "Category", "More info..." });
        for (Recipe recipe : recipes) {
            table.rows.add(
                new String[] {
                    String.valueOf(recipe.id),
                    recipe.name,
                    recipe.category,
                    "Run `get` sub-command for more info",
                }
            );
        }
        return table.toString();
    }

    @Command(name = "add", description = "Add a Recipe")
    @Override
    int add() {
        return userInteraction(
            scanner -> {
                ArrayList<FoodItem> ingredients = FoodItem.filter(
                    "select * from FoodItem",
                    stmt -> {}
                );
                List<Integer> possibleIngredientIds = getIngredientIds();
                Optional<String> recipeName = validatedString(
                    "Enter recipe name: ",
                    100,
                    true,
                    scanner
                );
                Optional<String> recipeCategory = validatedString(
                    "Enter the recipe category: ",
                    60,
                    true,
                    scanner
                );
                System.out.println("The following are all available recipe ingredients:");
                for (FoodItem ingredient : ingredients) {
                    System.out.printf("  %3d: %s\n", ingredient.id, ingredient.name);
                }
                Optional<List<Integer>> ingredientIds = validatedCommaSepPossibleInt(
                    "Enter comma-separated IDs of ingredients used in this recipe: ",
                    possibleIngredientIds,
                    true,
                    scanner
                );
                Optional<String> recipeInstructions = validatedMultilineString(
                    "Enter recipe instructions (type \"/<return>\" on a blank line to denote the end):\n",
                    true,
                    scanner
                );
                System.out.println("Saving to DB...");
                Recipe newItem = Recipe.create(
                    recipeName.get(),
                    recipeInstructions.get(),
                    recipeCategory.get()
                );
                saveIngredients(newItem.id, ingredientIds.get());
                return 0;
            }
        );
    }

    @Command(name = "list", description = "List recipes")
    @Override
    int list() {
        return userInteraction(
            scanner -> {
                ArrayList<Recipe> items = Recipe.filter("select * from Recipe", stmt -> {});
                System.out.println(getRecipeTable(items));
                return 0;
            }
        );
    }

    @Command(name = "get", description = "Get the details of a recipe")
    @Override
    int get() {
        return userInteraction(
            scanner -> {
                Optional<Integer> foodId = validatedPositiveInt(
                    "Enter the recipe ID to get: ",
                    true,
                    scanner
                );
                Optional<Recipe> recipe = Recipe.get(foodId.get());
                if (!recipe.isPresent()) {
                    System.out.println("ID doesn't exist. Try again.");
                    return 1;
                }
                Recipe recipeVal = recipe.get();
                List<FoodItem> ingredients = recipeVal.getFoodItems();
                System.out.printf("\nID: %s\n", recipeVal.id);
                System.out.printf("Name: %s\n", recipeVal.name);
                System.out.printf("Category: %s\n", recipeVal.category);
                System.out.println("Ingredients:");
                for (FoodItem ingredient : ingredients) {
                    System.out.printf("  %s\n", ingredient);
                }
                return 0;
            }
        );
    }

    @Command(name = "update", description = "Update a recipe's information")
    @Override
    int update() {
        return userInteraction(
            scanner -> {
                ArrayList<FoodItem> ingredients = FoodItem.filter(
                    "select * from FoodItem",
                    stmt -> {}
                );
                List<Integer> possibleIngredientIds = getIngredientIds();
                Optional<Integer> recipeId = validatedPositiveInt(
                    "Enter the recipe ID to update: ",
                    true,
                    scanner
                );
                Optional<Recipe> recipe = Recipe.get(recipeId.get());
                if (!recipe.isPresent()) {
                    System.out.println("ID doesn't exist. Try again.");
                    return 1;
                }
                Recipe recipeVal = recipe.get();
                Optional<String> recipeName = validatedString(
                    String.format("Enter the recipe name (\"%s\"): ", recipeVal.name),
                    100,
                    false,
                    scanner
                );
                if (recipeName.isPresent()) {
                    recipeVal.name = recipeName.get();
                }
                Optional<String> recipeCategory = validatedString(
                    String.format("Enter the recipe category (\"%s\"): ", recipeVal.category),
                    60,
                    false,
                    scanner
                );
                if (recipeCategory.isPresent()) {
                    recipeVal.category = recipeCategory.get();
                }
                System.out.println("The following are all available recipe ingredients:");
                for (FoodItem ingredient : ingredients) {
                    System.out.printf("  %3d: %s\n", ingredient.id, ingredient.name);
                }
                Optional<List<Integer>> ingredientIds = validatedCommaSepPossibleInt(
                    "Enter comma-separated IDs of ingredients used in this recipe (old ingredients have been removed): ",
                    possibleIngredientIds,
                    true,
                    scanner
                );
                Optional<String> recipeInstructions = validatedMultilineString(
                    String.format(
                        "Enter the recipe instructions (\"%s...\"):\n",
                        recipeVal.instructions
                            .substring(0, Math.min(20, recipeVal.instructions.length()))
                            .replace("\n", "\\n")
                    ),
                    false,
                    scanner
                );
                if (recipeInstructions.isPresent()) {
                    recipeVal.instructions = recipeInstructions.get();
                }
                System.out.println("Saving to DB...");
                recipeVal.update();
                saveIngredients(recipeVal.id, ingredientIds.get());
                return 0;
            }
        );
    }

    @Command(name = "delete", description = "Delete a recipe")
    @Override
    int delete() {
        return userInteraction(
            scanner -> {
                Optional<Integer> recipeId = validatedPositiveInt(
                    "Enter the recipe ID to delete: ",
                    true,
                    scanner
                );
                Optional<Recipe> recipe = Recipe.get(recipeId.get());
                if (!recipe.isPresent()) {
                    System.out.println("ID doesn't exist. Try again.");
                    return 1;
                }
                recipe.get().delete();
                return 0;
            }
        );
    }

    @Command(name = "search", description = "Search for a recipe")
    int search(
        @Option(
            names = "-i",
            description = "Ingredient filter string",
            defaultValue = ""
        ) String ingredient,
        @Option(
            names = "-c",
            description = "Category filter string",
            defaultValue = ""
        ) String category
    ) {
        if (ingredient.equals("") == category.equals("")) {
            System.err.println("Must provide one of [-c] or [-i] options");
            return 1;
        }
        try {
            //checks if category string from command is empty, if not, queries database for
            //recipe with a matching category
            if (category.isBlank() == false) {
                ArrayList<Recipe> recipes = Recipe.filter(
                    "select * from recipe where category=?",
                    stmt -> {
                        stmt.setString(1, category);
                    }
                );

                System.out.println(getRecipeTable(recipes));
            } else { //queries for recipe with matching matching ingredient name from the fooditem table
                ArrayList<Recipe> recipes = Recipe.filter(
                    "select r.* from recipe r join RecipeFoodItem rfi on r.id = rfi.recipeID join FoodItem fi on fi.id = rfi.foodItemID where fi.name = ?",
                    stmt -> {
                        stmt.setString(1, ingredient);
                    }
                );

                System.out.println(getRecipeTable(recipes));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }
}
