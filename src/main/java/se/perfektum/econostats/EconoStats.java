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
    private static final String TRANSACTIONS_JSON = "transactions.json";
    private static final String SPREADSHEET = "recurringTransactions.ods";
    private static final String TRANSACTIONS_PATH = LOCAL_FILES_FOLDER_NAME + TRANSACTIONS_JSON;
    private static final String RECURRING_TRANSACTIONS_PATH = LOCAL_FILES_FOLDER_NAME + SPREADSHEET;
    private static final String GOOGLE_DRIVE_FOLDER_NAME = "EconoStats";

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
        final String CSV_FILE = "c:/temp/testdata/nordeaGemensamt.csv";
        LOGGER.debug(String.format("Parsing file '%s'", CSV_FILE));
        List<AccountTransaction> importedAccountTransactions = csvReader.parseCsv(CSV_FILE, ",", new char[]{'"'});
        List<PayeeFilter> localPayeeFilters = getLocalPayeeFilters();

        String folderId = searchFile(GOOGLE_DRIVE_FOLDER_NAME, APPLICATION_VND_GOOGLE_APPS_FOLDER);

        File directory = new File(LOCAL_FILES_FOLDER_NAME);
        if (!directory.exists()) {
            LOGGER.debug(String.format("Local folder '%s' did not exist, creating folder.", LOCAL_FILES_FOLDER_NAME));
            directory.mkdir();
        }

        if (folderId == null) {
            LOGGER.debug(String.format("Google Drive folder '%s' did not exist, creating folder.", GOOGLE_DRIVE_FOLDER_NAME));
            folderId = accountTransactionDao.createFolder(GOOGLE_DRIVE_FOLDER_NAME);
        }

        String transactionFileId = searchFile(TRANSACTIONS_JSON, MimeTypes.APPLICATION_JSON.toString());
        String spreadsheetFileId = searchFile(SPREADSHEET, MimeTypes.GOOGLE_API_SPREADSHEET.toString());

        if (transactionFileId == null) {
            LOGGER.debug(String.format("File '%s' did not exist, no merge needed.", TRANSACTIONS_JSON));

            // save imported transactions locally
            LOGGER.debug(String.format("Storing file '%s' to local disk.", TRANSACTIONS_PATH));
            String convertedTransactions = convertTransactionsToJson(importedAccountTransactions);
            File filePathTransactions = saveFileLocally(TRANSACTIONS_PATH, convertedTransactions);

            // save imported transactions to Drive
            accountTransactionDao.createFile(filePathTransactions, Arrays.asList(folderId), MimeTypes.APPLICATION_JSON.toString(), MimeTypes.APPLICATION_JSON.toString());

            // create spreadsheet
            File filePathSpreadsheet = spreadsheetManager.createNewSpreadsheet(RECURRING_TRANSACTIONS_PATH, importedAccountTransactions, localPayeeFilters);

            // save/update spreadsheet to Drive
            if (spreadsheetFileId == null) {
                accountTransactionDao.createFile(filePathSpreadsheet, Arrays.asList(folderId), MimeTypes.GOOGLE_API_SPREADSHEET.toString(), MimeTypes.TEXT_ODS.toString());
            } else {
                accountTransactionDao.updateFile(spreadsheetFileId, new File(RECURRING_TRANSACTIONS_PATH), MimeTypes.TEXT_ODS.toString());
            }
        } else {
            // get transactions from Drive
            String transactions = accountTransactionDao.getFile(transactionFileId);

            // merge transactions with imported transactions
            List<AccountTransaction> mergedAccountTransactions = spreadsheetManager.mergeAccountTransactions(importedAccountTransactions, transactions);

            // save merged transactions locally
            String convertedTransactions = convertTransactionsToJson(mergedAccountTransactions);
            FileUtils.writeStringToFile(new java.io.File(TRANSACTIONS_PATH), convertedTransactions, "UTF-8");

            // overwrite transaction file on Drive
            accountTransactionDao.updateFile(transactionFileId, new File(TRANSACTIONS_PATH), MimeTypes.APPLICATION_JSON.toString());

            // create spreadsheet
            File filePathSpreadsheet = spreadsheetManager.createNewSpreadsheet(RECURRING_TRANSACTIONS_PATH, mergedAccountTransactions, localPayeeFilters);

            // save/update spreadsheet to Drive
            if (spreadsheetFileId == null) {
                accountTransactionDao.createFile(filePathSpreadsheet, Arrays.asList(folderId), MimeTypes.GOOGLE_API_SPREADSHEET.toString(), MimeTypes.TEXT_ODS.toString());
            } else {
                accountTransactionDao.updateFile(spreadsheetFileId, new File(RECURRING_TRANSACTIONS_PATH), MimeTypes.TEXT_ODS.toString());
            }
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
            LOGGER.debug(String.format("Could not find file '%s' on Google Drive", name));
            return null;
        } else if (items.size() > 1) {
            LOGGER.error("Inconsistency in file/folder structure. More than one item found! Please check folder/file structure!");
            throw new IOException("Inconsistency in file/folder structure. More than one item found! Please check folder/file structure!");
        }
        return items.get(0);
    }
}