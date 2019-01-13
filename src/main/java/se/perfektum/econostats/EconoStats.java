package se.perfektum.econostats;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.perfektum.econostats.bank.CsvReader;
import se.perfektum.econostats.common.JsonUtils;
import se.perfektum.econostats.configuration.AppProperties;
import se.perfektum.econostats.dao.AccountTransactionDao;
import se.perfektum.econostats.dao.googledrive.MimeTypes;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;
import se.perfektum.econostats.spreadsheet.SpreadsheetManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    private SpreadsheetManager spreadsheetManager;
    private CsvReader csvReader;
    private AccountTransactionDao accountTransactionDao;

    private String localFilesPath;
    private String transactionsFilename;
    private String spreadsheetFilename;
    private String googleDriveFolderName;
    private String transactionsPath;
    private String recurringTransactionsPath;
    private String payeeFiltersFilePath;

    public EconoStats(SpreadsheetManager spreadsheetManager, CsvReader csvReader, AccountTransactionDao accountTransactionDao, AppProperties appProperties) {
        this.spreadsheetManager = spreadsheetManager;
        this.csvReader = csvReader;
        this.accountTransactionDao = accountTransactionDao;

        initProperties(appProperties);
    }

    public void start() throws Exception {
        List<AccountTransaction> importedAccountTransactions = csvReader.parseCsv();
        List<PayeeFilter> localPayeeFilters = getLocalPayeeFilters();

        String folderId = searchFile(googleDriveFolderName, APPLICATION_VND_GOOGLE_APPS_FOLDER);

        File directory = new File(localFilesPath);
        if (!directory.exists()) {
            LOGGER.debug(String.format("Local folder '%s' did not exist, creating folder.", localFilesPath));
            directory.mkdir();
        }

        if (folderId == null) {
            LOGGER.debug(String.format("Google Drive folder '%s' did not exist, creating folder.", googleDriveFolderName));
            folderId = accountTransactionDao.createFolder(googleDriveFolderName);
        }

        String transactionFileId = searchFile(transactionsFilename, MimeTypes.APPLICATION_JSON.toString());
        String spreadsheetFileId = searchFile(spreadsheetFilename, MimeTypes.GOOGLE_API_SPREADSHEET.toString());

        if (transactionFileId == null) {
            LOGGER.debug(String.format("File '%s' did not exist, no merge needed.", transactionsFilename));

            // save imported transactions locally
            LOGGER.debug(String.format("Storing file '%s' to local disk.", transactionsPath));
            String convertedTransactions = convertTransactionsToJson(importedAccountTransactions);
            File filePathTransactions = saveFileLocally(transactionsPath, convertedTransactions);

            // save imported transactions to Drive
            accountTransactionDao.createFile(filePathTransactions, Arrays.asList(folderId), MimeTypes.APPLICATION_JSON.toString(), MimeTypes.APPLICATION_JSON.toString());

            // create spreadsheet
            File filePathSpreadsheet = spreadsheetManager.createNewSpreadsheet(recurringTransactionsPath, importedAccountTransactions, localPayeeFilters);

            // save/update spreadsheet to Drive
            if (spreadsheetFileId == null) {
                accountTransactionDao.createFile(filePathSpreadsheet, Arrays.asList(folderId), MimeTypes.GOOGLE_API_SPREADSHEET.toString(), MimeTypes.TEXT_ODS.toString());
            } else {
                accountTransactionDao.updateFile(spreadsheetFileId, new File(recurringTransactionsPath), MimeTypes.TEXT_ODS.toString());
            }
        } else {
            // get transactions from Drive
            String transactions = accountTransactionDao.getFile(transactionFileId);

            // merge transactions with imported transactions
            List<AccountTransaction> mergedAccountTransactions = spreadsheetManager.mergeAccountTransactions(importedAccountTransactions, transactions);

            // save merged transactions locally
            String convertedTransactions = convertTransactionsToJson(mergedAccountTransactions);
            FileUtils.writeStringToFile(new java.io.File(transactionsPath), convertedTransactions, "UTF-8");

            // overwrite transaction file on Drive
            accountTransactionDao.updateFile(transactionFileId, new File(transactionsPath), MimeTypes.APPLICATION_JSON.toString());

            // create spreadsheet
            File filePathSpreadsheet = spreadsheetManager.createNewSpreadsheet(recurringTransactionsPath, mergedAccountTransactions, localPayeeFilters);

            // save/update spreadsheet to Drive
            if (spreadsheetFileId == null) {
                accountTransactionDao.createFile(filePathSpreadsheet, Arrays.asList(folderId), MimeTypes.GOOGLE_API_SPREADSHEET.toString(), MimeTypes.TEXT_ODS.toString());
            } else {
                accountTransactionDao.updateFile(spreadsheetFileId, new File(recurringTransactionsPath), MimeTypes.TEXT_ODS.toString());
            }
        }
    }

    private void initProperties(AppProperties appProperties) {
        localFilesPath = appProperties.getOutputFilesPath();
        transactionsFilename = appProperties.getTransactionsFilename();
        spreadsheetFilename = appProperties.getSpreadsheetFilename();
        googleDriveFolderName = appProperties.getGoogleDriveFolderName();
        transactionsPath = appProperties.getTransactionsPath();
        recurringTransactionsPath = appProperties.getRecurringTransactionsPath();
        payeeFiltersFilePath = appProperties.getPayeeFiltersFilePath();
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
        String pFilters = new String(Files.readAllBytes(Paths.get(payeeFiltersFilePath)));
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