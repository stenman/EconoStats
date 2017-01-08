import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * The main class of this application
 */
public class EconoStatsMain {
    public static void main(String args[]) {

        final Logger LOGGER = LoggerFactory.getLogger(EconoStatsMain.class);

        DatabaseConnector.createNewTable();


        //TODO: Make this dynamic
//        final String CSV_FILE = "c:/temp/testdata/export.csv";
//
//        BufferedReader br = null;

//        try {
//            FileReader fr = new FileReader(CSV_FILE);
//            br = new BufferedReader(fr);
//        } catch (FileNotFoundException e) {
//            LOGGER.debug("Could not find file " + CSV_FILE);
//            System.exit(1);
//        }
//
//        if (br != null) {
//            try {
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            LOGGER.debug("File " + CSV_FILE + " could not be read.");
//        }
    }
}
