package se.perfektum.econostats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.perfektum.econostats.bank.CsvReader;
import se.perfektum.econostats.bank.nordea.NordeaCsvReader;
import se.perfektum.econostats.dao.AccountTransactionDao;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.spreadsheet.SpreadsheetManager;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * The main class of this application
 */
public class EconoStats {
    final Logger LOGGER = LoggerFactory.getLogger(EconoStats.class);

    private SpreadsheetManager spreadsheetManager;
    private CsvReader csvReader;
    private AccountTransactionDao accountTransactionDao;

    public EconoStats(SpreadsheetManager spreadSheetManager, CsvReader csvReader, AccountTransactionDao accountTransactionDao) {
        this.spreadsheetManager = spreadsheetManager;
        this.csvReader = csvReader;
        this.accountTransactionDao = accountTransactionDao;
    }

    public void start() throws Exception {
        // Read CSV and put into object list
        //TODO: Put in config
//        final String CSV_FILE = "c:/temp/testdata/export.csv";
//        List<AccountTransaction> importedAccountTransactions = csvReader.parseCsv(CSV_FILE, ",", new char[]{'"'});

        // Download existing JSON from GDrive
//        List<AccountTransaction> storedAccountTransactions = accountTransactionDao.loadAccountTransactions();
//        for (AccountTransaction storedAccountTransaction : storedAccountTransactions) {
//            System.out.println(storedAccountTransaction.getName());
//        }

        // Store full JSON on Drive
        System.out.println("File ID: " + accountTransactionDao.storeAccountTransactions());

    }
}