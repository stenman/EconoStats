import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class of this application
 */
public class EconoStatsMain {
    public static void main(String args[]) {

        final Logger LOGGER = LoggerFactory.getLogger(EconoStatsMain.class);

        //TODO: Make this dynamic
        final String CSV_FILE = "c:/temp/testdata/export.csv";

        //TODO: General goal 1a --> Read transactions from CSV into DB
        //TODO: goal 1b --> handle redundant csv reads (same file twice)

        //TODO: General goal 2 --> Use data from DB (write test with dummy data first) to create NEW csv that look and...
        //TODO: ... work exactly like the Google drive spreadsheet

        //TODO: General goal 3 --> find a good statistics tool and display some nice stats... start with a pie chart!

        //TODO: General goal 4 -->

        //TODO: Read csv file
        //TODO: Put info into DB
        //TODO: Create spredsheet/csv(?) from data
        //TODO: In the first version, just use premade constants like "FOLKSAM_HOUSE_LOAN"

        DatabaseConnector.createTables();

//        try {
//            FileReader fr = new FileReader(CSV_FILE);
//            BufferedReader br = new BufferedReader(fr);
//            while (br.readLine() != null) {
//                System.out.println(br.readLine());
//            }
//        } catch (FileNotFoundException e) {
//            LOGGER.debug("Add error message");
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
