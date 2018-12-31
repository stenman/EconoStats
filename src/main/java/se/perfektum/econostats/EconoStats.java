package se.perfektum.econostats;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.perfektum.econostats.bank.CsvReader;
import se.perfektum.econostats.common.JsonUtils;
import se.perfektum.econostats.dao.AccountTransactionDao;
import se.perfektum.econostats.dao.googledrive.MimeTypes;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;
import se.perfektum.econostats.spreadsheet.SpreadsheetManager;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static se.perfektum.econostats.dao.googledrive.GoogleDriveDao.APPLICATION_VND_GOOGLE_APPS_FOLDER;

/**
 * The main class of this application
 */
public class EconoStats {
    final Logger LOGGER = LoggerFactory.getLogger(EconoStats.class);

    private static final String LOCAL_FILES_FOLDER_NAME = "files/";
    private static final String TRANSACTIONS_PATH = LOCAL_FILES_FOLDER_NAME + "transactions.json";
    private static final String RECURRING_TRANSACTIONS_PATH = LOCAL_FILES_FOLDER_NAME + "recurringTransactions.ods";
    private static final String GOOGLE_DRIVE_FOLDER_NAME = "EconoStats";
    private static final String TRANSACTIONS_JSON = "transactions.json";

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
        final String CSV_FILE = "c:/temp/testdata/export-0.csv";
        List<AccountTransaction> importedAccountTransactions = csvReader.parseCsv(CSV_FILE, ",", new char[]{'"'});
        List<PayeeFilter> localPayeeFilters = getLocalPayeeFilters();

        String folderId = searchFile(GOOGLE_DRIVE_FOLDER_NAME, APPLICATION_VND_GOOGLE_APPS_FOLDER);

        File directory = new File(LOCAL_FILES_FOLDER_NAME);
        if (!directory.exists()) {
            LOGGER.debug("Local folder " + LOCAL_FILES_FOLDER_NAME + " did not exist, creating folder.");
            directory.mkdir();
        }

        if (folderId == null) {
            LOGGER.debug("Google Drive folder " + GOOGLE_DRIVE_FOLDER_NAME + " did not exist, creating folder.");
            folderId = accountTransactionDao.createFolder(GOOGLE_DRIVE_FOLDER_NAME);
        }

        String fileId = searchFile(TRANSACTIONS_JSON, MimeTypes.APPLICATION_JSON.toString());

        if (fileId == null) {
            LOGGER.debug("File " + TRANSACTIONS_JSON + " did not exist, no merge needed.");

            // save imported transactions locally
            String convertedTransactions = convertTransactionsToJson(importedAccountTransactions);
            File filePathTransactions = saveFileLocally(TRANSACTIONS_PATH, convertedTransactions);

            // save imported transactions to Drive
            accountTransactionDao.createFile(filePathTransactions, Arrays.asList(folderId), MimeTypes.APPLICATION_JSON.toString(), MimeTypes.APPLICATION_JSON.toString());

            // create spreadsheet
            File filePathSpreadsheet = spreadsheetManager.createNewSpreadsheet(RECURRING_TRANSACTIONS_PATH, importedAccountTransactions, localPayeeFilters);

            // save spreadsheet to Drive
            accountTransactionDao.createFile(filePathSpreadsheet, Arrays.asList(folderId), MimeTypes.GOOGLE_API_SPREADSHEET.toString(), MimeTypes.TEXT_ODS.toString());
        } else {
            // get transactions from Drive
            String transactions = accountTransactionDao.getFile(fileId);

            // merge transactions with imported transactions
            List<AccountTransaction> mergedAccountTransactions = spreadsheetManager.mergeAccountTransactions(importedAccountTransactions, transactions);

            // save merged transactions locally
            String convertedTransactions = convertTransactionsToJson(mergedAccountTransactions);
            FileUtils.writeStringToFile(new java.io.File(TRANSACTIONS_PATH), convertedTransactions, "UTF-8");

            // overwrite transaction file on Drive
            accountTransactionDao.updateFile(fileId, new File(TRANSACTIONS_PATH), MimeTypes.APPLICATION_JSON.toString());

            // create spreadsheet
            spreadsheetManager.createNewSpreadsheet(RECURRING_TRANSACTIONS_PATH, mergedAccountTransactions, localPayeeFilters);

            // overwrite spreadsheet on Drive
            accountTransactionDao.updateFile(fileId, new File(RECURRING_TRANSACTIONS_PATH), MimeTypes.TEXT_ODS.toString());
        }
    }

    private String convertTransactionsToJson(List<AccountTransaction> transactions) {
        Map m = new TreeMap<>();
        m.put("accountTransactions", transactions);
        return new Gson().toJson(m);
    }

    private File saveFileLocally(String filePath, String content) throws IOException {
        FileUtils.writeStringToFile(new File(filePath), content, "UTF-8");
        return new File(filePath);
    }

    private List<PayeeFilter> getLocalPayeeFilters() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        String pFilters = IOUtils.toString(classLoader.getResourceAsStream("payeeFilters.json"), "UTF-8");
        return JsonUtils.getJsonElement(PayeeFilter.class, pFilters);
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