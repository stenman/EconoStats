import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.perfektum.econostats.bank.nordea.NordeaCsvReader;
import se.perfektum.econostats.domain.AccountTransaction;

import java.util.ArrayList;
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
        List<AccountTransaction> accountTransactions = new ArrayList<>();
        NordeaCsvReader nordeaCsvReader = new NordeaCsvReader();
        try {
            accountTransactions = nordeaCsvReader.parseCsv(CSV_FILE, ",", new char[]{'"'});
        } catch (Exception e) {
            e.printStackTrace();
        }

//        se.perfektum.econostats.dao.DatabaseConnector.createTables();

        //TODO: 2. create new csv
        //TODO: create headers
        //TODO: sort List<accountTransaction> by date
        //TODO: for each object in List<accountTransaction>, insert into new csv (rows=trans, cols=month)

        //TODO: 3. handle redundant csv reads (eg. same file twice)
        //TODO: (this requires that all objects are saved into DB in "1. read csv")
        //TODO: check DB for identical transactions --> don't add those to csv, but add all others

        //TODO: 4. find a good statistics tool and display some nice stats... start with a pie chart!
    }
}
