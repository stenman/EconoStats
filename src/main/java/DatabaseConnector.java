import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static java.sql.DriverManager.*;

/**
 * Created by stenman on 2017-01-08.
 */
public class DatabaseConnector {
    public static void createNewTable() {
        File directory = new File(String.valueOf("sqlite"));
        if (!directory.exists()) {
            directory.mkdir();
        }

        String url = "jdbc:sqlite:sqlite/econostats.db";

        String sql = "CREATE TABLE IF NOT EXISTS warehouses (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL,\n"
                + "	capacity real\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
