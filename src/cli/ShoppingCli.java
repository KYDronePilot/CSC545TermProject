package cli;

import database.Database;
import java.sql.SQLException;
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
        System.out.println("Food needed this week that we don't have:");
        try {
            Database db = Database.getInstance();
            db.select(
                "SELECT distinct fooditem.name as name FROM fooditem INNER JOIN recipefooditem ON recipefooditem.fooditemid = fooditem.id INNER JOIN recipemealplan ON recipemealplan.recipeid = recipefooditem.recipeid WHERE fooditem.units = 0",
                rs -> {
                    System.out.println(rs.getString("name"));
                }
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }
}
