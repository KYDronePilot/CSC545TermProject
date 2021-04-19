package cli;

import java.util.concurrent.Callable;
import picocli.CommandLine.Command;

/**
 * CLI for generating a shopping list.
 */
@Command(
    name = "shopping",
    description = "Generate a shopping list based on the current meal plans",
    mixinStandardHelpOptions = true
)
public class ShoppingCli implements Callable<Integer> {

    @Override
    public Integer call() {
        System.out.println("Here is your shopping list: ...");
        return 0;
    }
}
