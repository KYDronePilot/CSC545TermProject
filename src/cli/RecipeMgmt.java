package cli;

import database.Database;
import java.sql.SQLException;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Main CLI entry point for application.
 */
@Command(
    name = "recipe_mgmt",
    subcommands = { FoodItemCli.class, RecipeCli.class, MealPlanCli.class, ShoppingCli.class },
    mixinStandardHelpOptions = true,
    description = "Manage recipes, meal plans, and a shopping list for items needed.",
    version = "1.0.0"
)
public class RecipeMgmt {

    public static void main(String... args) {
        int exitCode = 1;
        try (Database db = Database.getInstance()) {
            exitCode = new CommandLine(new RecipeMgmt()).execute(args);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.exit(exitCode);
    }
}
