package se.perfektum.econostats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import se.perfektum.econostats.bank.nordea.NordeaCsvReader;
import se.perfektum.econostats.spreadsheet.ISpreadsheetManager;
import se.perfektum.econostats.spreadsheet.SpreadsheetManager;
import se.perfektum.econostats.domain.AccountTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * The main class of this application
 */
public class EconoStats {
    public static void mainTemp(String args[]) {

        // [x]  read CSV
        // [ ]  create object for each transaction in CSV, put each object in List<accountTransaction>
        // [ ]  download existing JSON from /economy folder (if exists)
        // [ ]  convert existing JSON into another List<AccountTransaction>
        // [ ]  compare new and old List<AccountTransaction>:
        // [ ]       merge the two lists (insert (into a "full" list) all objects that are not redundant)
        // [ ]       create a full JSON from the full list
        // [ ]       save the full JSON to drive (overwrite old)
        // [ ]  remove all objects from list that do not exist in premade configuration list of names
        // [ ]  use the new list to create ODF file
        // [ ]  save ODF file to /economy folder
        //
        // [ ]  future: LOG all removals etc.!
        // [ ]  future: use config file, save in /economy folder
        // [ ]  find a good statistics tool and display some nice stats... start with a pie chart!

        ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");

        ISpreadsheetManager spredsheetManager = (SpreadsheetManager) context.getBean("spreadsheetManager");

        final Logger LOGGER = LoggerFactory.getLogger(EconoStats.class);

        //TODO: Put in config
        final String CSV_FILE = "c:/temp/testdata/export.csv";

        List<AccountTransaction> accountTransactions = new ArrayList<>();
        NordeaCsvReader nordeaCsvReader = new NordeaCsvReader();
        try {
            accountTransactions = nordeaCsvReader.parseCsv(CSV_FILE, ",", new char[]{'"'});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
