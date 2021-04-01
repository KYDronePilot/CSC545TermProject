public class App {

    public static void main(String[] args) throws Exception {
        var db = Database.getInstance();
        db.select(
            "SELECT cno FROM CoursesDescription",
            rs -> {
                System.out.println(rs.getString("cno"));
            }
        );
    }
}
