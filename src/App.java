import java.sql.Types;
import java.util.ArrayList;
import models.NutritionFacts;

public class App {

    public static void main(String[] args) throws Exception {
        // Database db = Database.getInstance();
        NutritionFacts facts = NutritionFacts.create(1, 2, 3, 4, 5);
        System.out.println(facts.id);
        System.out.println(facts.sugar);
        ArrayList<NutritionFacts> items = NutritionFacts.filter(
            "select * from NutritionFacts where id = ?",
            stmt -> {
                stmt.setInt(1, 12);
            }
        );
        NutritionFacts item = items.get(0);
        item.delete();
        // item.calories = 100;
        // item.update();
        // for (NutritionFacts item : items) {
        //     System.out.println(item.id);
        // }
        // System.out.println(db.getClass().getDeclaredFields()[0].getName());
        // db.select(
        //     "SELECT cno FROM CoursesDescription",
        //     rs -> {
        //         System.out.println(rs.getString("cno"));
        //     }
        // );
        // db.modify(
        //     "insert into CoursesDescription values (?,?,?,?,?)",
        //     stmt -> {
        //         stmt.setString(1, "tst101");
        //         stmt.setString(2, "Test Course");
        //         stmt.setString(3, "TST");
        //         stmt.setFloat(4, 3.5f);
        //         stmt.setNull(5, Types.NULL);
        //     }
        // );
        // db.select(
        //     "SELECT cno FROM CoursesDescription WHERE cno = ?",
        //     rs -> {
        //         System.out.println(rs.getString("cno"));
        //     },
        //     stmt -> {
        //         stmt.setString(1, "tst100");
        //     }
        // );
    }
}
