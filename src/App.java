import java.sql.Types;

public class App {

    public static void main(String[] args) throws Exception {
        var db = Database.getInstance();
        db.select(
            "SELECT cno FROM CoursesDescription",
            rs -> {
                System.out.println(rs.getString("cno"));
            }
        );
        // db.modify(
        //     "insert into CoursesDescription values (?,?,?,?,?)",
        //     stmt -> {
        //         stmt.setString(1, "tst100");
        //         stmt.setString(2, "Test Course");
        //         stmt.setString(3, "TST");
        //         stmt.setInt(4, 3);
        //         stmt.setNull(5, Types.NULL);
        //     }
        // );
        db.select(
            "SELECT cno FROM CoursesDescription WHERE cno = ?",
            rs -> {
                System.out.println(rs.getString("cno"));
            },
            stmt -> {
                stmt.setString(1, "tst100");
            }
        );
    }
}
