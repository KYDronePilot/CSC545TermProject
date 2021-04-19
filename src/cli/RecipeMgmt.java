package cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Main CLI entry point for application.
 */
@Command(
    name = "recipe_mgmt",
    subcommands = { FoodItemCli.class, RecipeCli.class, MealPlanCli.class }
)
public class RecipeMgmt {

    // @Option(names = "-v")
    // int verbose;

    public static void main(String... args) {
        var exitCode = new CommandLine(new RecipeMgmt()).execute(args);
        System.exit(exitCode);
    }
}
