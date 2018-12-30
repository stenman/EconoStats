package se.perfektum.econostats;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.perfektum.econostats.bank.CsvReader;
import se.perfektum.econostats.bank.nordea.NordeaCsvReader;
import se.perfektum.econostats.dao.AccountTransactionDao;
import se.perfektum.econostats.dao.googledrive.GoogleDriveDao;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;
import se.perfektum.econostats.spreadsheet.SpreadsheetManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.gson.Gson.*;
import static se.perfektum.econostats.dao.googledrive.GoogleDriveDao.APPLICATION_VND_GOOGLE_APPS_FILE;
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

    public EconoStats(SpreadsheetManager spreadsheetManager, CsvReader csvReader, AccountTransactionDao accountTransactionDao) {
        this.spreadsheetManager = spreadsheetManager;
        this.csvReader = csvReader;
        this.accountTransactionDao = accountTransactionDao;
    }

    public void start() throws Exception {
        //TODO: Put in config
        final String CSV_FILE = "c:/temp/testdata/export.csv";
        List<AccountTransaction> importedAccountTransactions = csvReader.parseCsv(CSV_FILE, ",", new char[]{'"'});

        String folderId = searchFile(FOLDER_NAME, APPLICATION_VND_GOOGLE_APPS_FOLDER);

        if (folderId == null) {
            LOGGER.debug("Folder " + FOLDER_NAME + " did not exist, creating folder.");
            folderId = accountTransactionDao.createFolder(FOLDER_NAME);
        }
        String fileId = searchFile(JSON_NAME, APPLICATION_VND_GOOGLE_APPS_FILE);
        if (fileId == null) {
            LOGGER.debug("File " + JSON_NAME + " did not exist, no merge needed.");
            // *** convert csv to List<AT> to json ***
            String importedTransactions = new Gson().toJson(importedAccountTransactions);

            // *** do all the spreadsheet magic! ***
            spreadsheetManager.createSpreadsheet();

            accountTransactionDao.createFile(importedTransactions, Arrays.asList(folderId));
            // save recurringTransactions.odf to Drive
        } else {
            // download file from Drive
            String downloadedAccountTransactions = accountTransactionDao.getFile(fileId);

            // *** convert csv to List<AT> to json ***
            // *** read transaction.json ***
            // *** do some "merge magic" with the two jsons or convert them both to lists and compare/merge... ***
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