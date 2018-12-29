package se.perfektum.econostats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.perfektum.econostats.bank.CsvReader;
import se.perfektum.econostats.bank.nordea.NordeaCsvReader;
import se.perfektum.econostats.dao.AccountTransactionDao;
import se.perfektum.econostats.dao.googledrive.GoogleDriveDao;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.spreadsheet.SpreadsheetManager;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static se.perfektum.econostats.dao.googledrive.GoogleDriveDao.APPLICATION_VND_GOOGLE_APPS_FOLDER;

/**
 * The main class of this application
 */
public class EconoStats {
    final Logger LOGGER = LoggerFactory.getLogger(EconoStats.class);

    private static final String FOLDER_NAME = "EconoStats";
    private static final String JSON_NAME = "transactions.json";

    private SpreadsheetManager spreadsheetManager;
    private CsvReader csvReader;
    private AccountTransactionDao accountTransactionDao;

    public EconoStats(SpreadsheetManager spreadSheetManager, CsvReader csvReader, AccountTransactionDao accountTransactionDao) {
        this.spreadsheetManager = spreadsheetManager;
        this.csvReader = csvReader;
        this.accountTransactionDao = accountTransactionDao;
    }

    public void start() throws Exception {
        // *** read csv file and put into object list ***
        //TODO: Put in config
//        final String CSV_FILE = "c:/temp/testdata/export.csv";
//        List<AccountTransaction> importedAccountTransactions = csvReader.parseCsv(CSV_FILE, ",", new char[]{'"'});


        String folderId = searchFile(FOLDER_NAME, APPLICATION_VND_GOOGLE_APPS_FOLDER);

        if (folderId == null) {
            //TODO: Log this
            System.out.println("Folder " + FOLDER_NAME + " did not exist, creating folder...");
            folderId = accountTransactionDao.createFolder(FOLDER_NAME); //save ID!!
        }
        String fileId = searchFile(JSON_NAME, APPLICATION_VND_GOOGLE_APPS_FOLDER);
        if (fileId == null) {
            // *** convert csv to List<AT> to json ***
            // *** do all the spreadsheet magic! ***
            // *** NOTE: exclude from odf: all objects that do not exist in premade configuration list of names ***
            accountTransactionDao.createFile(Arrays.asList(folderId));
            // save recurringTransactions.odf to Drive
        } else {
            // download file from Drive
            System.out.println(accountTransactionDao.getFile(fileId));

            // *** convert csv to List<AT> to json ***
            // *** read transaction.json ***
            // *** do some "merge magic" with the two jsons or convert them both to lists and compare/merge... ***
            // *** NOTE: exclude from odf: all objects that do not exist in premade configuration list of names ***
            accountTransactionDao.updateFile(fileId);
            // "overwrite" recurringTransactions.odf on Drive
        }
    }

    //TODO: make optional
    private String searchFile(String name, String mimeType) throws IOException, GeneralSecurityException {
        List<String> items = accountTransactionDao.searchForFile(name, mimeType);

        if (items.isEmpty()) {
            System.out.println("No match!");
            return null;
        } else if (items.size() > 1) {
            //TODO: Log this properly
            //TODO: Throw a sensible exception here!
            System.out.println("Inconsistency in file/folder structure. More than one item found! Please check folder/file structure!");
            throw new IOException();
        }
        return items.get(0);
    }
}