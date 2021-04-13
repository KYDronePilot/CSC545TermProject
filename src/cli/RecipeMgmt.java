package cli;

import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Main CLI entry point for application.
 */
@Command(name = "recipe_mgmt", subcommands = { FoodItemCli.class })
public class RecipeMgmt implements Callable<Integer> {

    // @Option(names = "-v")
    // int verbose;

    // FIXME
    @Override
    public Integer call() {
        System.out.println("Hi from test: ");
        return 0;
    }

    public static void main(String... args) {
        var exitCode = new CommandLine(new RecipeMgmt()).execute(args);
        System.out.println(exitCode);
    }
}
