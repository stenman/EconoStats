import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Please add documentation to this class
 */
public class DatabaseConnector {

    //TODO: make property
    private static final String ECONOSTATS_DB = "jdbc:sqlite:C:/development/sqlite/econostats.db";

    //TODO: id account date transaction category amount balance dateInserted dateChanged
    public static void createTables() {

        String user = "CREATE TABLE IF NOT EXISTS user (\n"
                + "	userId integer NOT NULL,\n"
                + "	name text NOT NULL\n"
                + ");";

        String account = "CREATE TABLE IF NOT EXISTS account (\n"
                + "	userId integer PRIMARY KEY,\n"
                + "	accountId integer NOT NULL,\n"
                + "	accountName text NOT NULL\n"
                + ");";

        String category = "CREATE TABLE IF NOT EXISTS category (\n"
                + "	categoryId integer PRIMARY KEY,\n"
                + "	categoryName text NOT NULL\n"
                + ");";

        String accountTransaction = "CREATE TABLE IF NOT EXISTS accountTransaction (\n"
                + "	userId integer NOT NULL,\n"
                + "	accountId integer NOT NULL,\n"
                + "	date text NOT NULL,\n"
                + "	name text NOT NULL,\n"
                + "	categoryId integer NOT NULL,\n"
                + "	amount integer NOT NULL,\n"
                + "	balance integer NOT NULL,\n"
                + "	dateInserted text NOT NULL,\n"
                + "	dateChanged text NOT NULL\n"
                + ");";

        try (
                Connection conn = DriverManager.getConnection(ECONOSTATS_DB);
                Statement stmt = conn.createStatement()) {
            stmt.execute(user);
            stmt.execute(account);
            stmt.execute(category);
            stmt.execute(accountTransaction);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
