import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * The main class of this application
 */
public class EconoStatsMain {
    public static void main(String args[]) {

        final Logger LOGGER = LoggerFactory.getLogger(EconoStatsMain.class);

        //TODO: Make this dynamic
        final String CSV_FILE = "c:/development/testdata/export.csv";

        try {
            FileReader fr = new FileReader(CSV_FILE);
            BufferedReader br = new BufferedReader(fr);
        } catch (FileNotFoundException e) {
            LOGGER.debug("Add error message");
            e.printStackTrace();
        }

    }
}
