import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The main class of this application
 */
public class EconoStatsMain {
    public static void main(String args[]) {

        final Logger LOGGER = LoggerFactory.getLogger(EconoStatsMain.class);

        //TODO: Make this dynamic
        final String CSV_FILE = "c:/temp/testdata/export.csv";

        //TODO: 1. read csv
        //TODO: create object for each transaction in csv, put each object in List<accountTransaction>
        //TODO: (later/bonus) insert all objects into DB
        //TODO: remove all objects from list that do not exist in premade list of names (don't forget to LOG the removals!)
        //TODO: now we should have a list of all transactions that should be in the new CSV

        CsvReader cr = new CsvReader();
        List<String[]> lines = null;
        try {
            lines = cr.parseCsv(CSV_FILE, ";");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Datum,Transaktion,Kategori,Belopp,Saldo
        for (String[] line : lines) {
//            AccountTransaction at = new AccountTransaction();
//            at.setUserId(1); //TODO: fix this
//            at.setAccountId(1); //TODO: fix this
//            at.setStampInserted(LocalDateTime.now()); //TODO: is this really necessary?
//            at.setStampChanged(LocalDateTime.now()); //TODO: is this really necessary?
//            at.setDate(line);
//            at.setName();
//            at.setCategoryId();
//            at.setAmount();
//            at.setBalance();
        }


        //TODO: 2. create new csv
        //TODO: create headers
        //TODO: sort List<accountTransaction> by date
        //TODO: for each object in List<accountTransaction>, insert into new csv (rows=trans, cols=month)

        //TODO: 3. handle redundant csv reads (eg. same file twice)
        //TODO: (this requires that all objects are saved into DB in "1. read csv")
        //TODO: check DB for identical transactions --> don't add those to csv, but add all others

        //TODO: 4. find a good statistics tool and display some nice stats... start with a pie chart!

//        DatabaseConnector.createTables();

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
