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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.*;

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
    private String storagePath;
    private String transactionsPath;
    private String recurringTransactionsPath;
    private String payeeFiltersFileName;

    private static final String ACCOUNT_TRANSACTIONS = "accountTransactions";

    public EconoStats(SpreadsheetManager spreadsheetManager, CsvReader csvReader, AccountTransactionDao accountTransactionDao, AppProperties appProperties) {
        this.spreadsheetManager = spreadsheetManager;
        this.csvReader = csvReader;
        this.accountTransactionDao = accountTransactionDao;

        initProperties(appProperties);
    }

    public List<AccountTransaction> getAccountTransactions() {
        try {
            return csvReader.getAccountTransactionsFromFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<PayeeFilter> getPayeeFilters() {
        try {
            String fileId = getFileId(payeeFiltersFileName, MimeTypes.APPLICATION_JSON.toString());
            String filters = accountTransactionDao.getFile(fileId);
            return filters == null ? null : JsonUtils.getJsonElement(PayeeFilter.class, filters);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public void generateRecurringTransactions(List<PayeeFilter> payeeFilters, List<AccountTransaction> accountTransactions) throws Exception {
        String folderId = getFileId(storagePath, APPLICATION_VND_GOOGLE_APPS_FOLDER);

        File directory = new File(localFilesPath);
        if (!directory.exists()) {
            LOGGER.debug(String.format("Local folder '%s' did not exist, creating folder.", localFilesPath));
            directory.mkdir();
        }

        if (folderId == null) {
            LOGGER.debug(String.format("Google Drive folder '%s' did not exist, creating folder.", storagePath));
            folderId = accountTransactionDao.createFolder(storagePath);
        }

        String transactionFileId = getFileId(transactionsFilename, MimeTypes.APPLICATION_JSON.toString());
        String spreadsheetFileId = getFileId(spreadsheetFilename, MimeTypes.GOOGLE_API_SPREADSHEET.toString());

        if (transactionFileId == null) {
            LOGGER.debug(String.format("File '%s' did not exist, no merge needed.", transactionsFilename));

            // save imported transactions locally
            LOGGER.debug(String.format("Storing file '%s' to local disk.", transactionsPath));
            String convertedTransactions = convertObjectsToJson(accountTransactions, ACCOUNT_TRANSACTIONS);
            File filePathTransactions = saveFileLocally(transactionsPath, convertedTransactions);

            // save imported transactions to Drive
            accountTransactionDao.createFile(filePathTransactions, Arrays.asList(folderId), MimeTypes.APPLICATION_JSON.toString(), MimeTypes.APPLICATION_JSON.toString());

            // create spreadsheet
            File filePathSpreadsheet = spreadsheetManager.createNewSpreadsheet(recurringTransactionsPath, accountTransactions, payeeFilters);

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
            List<AccountTransaction> mergedAccountTransactions = spreadsheetManager.mergeAccountTransactions(accountTransactions, transactions);

            // save merged transactions locally
            String convertedTransactions = convertObjectsToJson(mergedAccountTransactions, ACCOUNT_TRANSACTIONS);
            FileUtils.writeStringToFile(new java.io.File(transactionsPath), convertedTransactions, "UTF-8");

            // overwrite transaction file on Drive
            accountTransactionDao.updateFile(transactionFileId, new File(transactionsPath), MimeTypes.APPLICATION_JSON.toString());

            // create spreadsheet
            File filePathSpreadsheet = spreadsheetManager.createNewSpreadsheet(recurringTransactionsPath, mergedAccountTransactions, payeeFilters);

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
        storagePath = appProperties.getStoragePath();
        transactionsPath = appProperties.getTransactionsPath();
        recurringTransactionsPath = appProperties.getRecurringTransactionsPath();
        payeeFiltersFileName = appProperties.getPayeeFiltersFileName();
    }

    private String convertObjectsToJson(List<?> json, String rootElement) {
        Map m = new TreeMap<>();
        m.put(rootElement, json);
        return new Gson().toJson(m);
    }

    private File saveFileLocally(String filePath, String content) throws IOException {
        FileUtils.writeStringToFile(new File(filePath), content, "UTF-8");
        return new File(filePath);
    }

    /**
     * Searches for a file in storage.
     */
    //TODO: make optional
    //TODO: This is Google Drive, not general. Make general.
    //TODO: Logs and exceptions should probably be moved to storage area
    private String getFileId(String name, String mimeType) throws IOException, GeneralSecurityException {
        List<String> items = accountTransactionDao.searchForFile(name, mimeType);

        if (items.isEmpty()) {
            LOGGER.debug(String.format("Could not find file '%s' in storage", name));
            return null;
        } else if (items.size() > 1) {
            LOGGER.error("Inconsistency in file/folder structure. More than one item found! Please check folder/file structure!");
            throw new IOException("Inconsistency in file/folder structure. More than one item found! Please check folder/file structure!");
        }
        return items.get(0);
    }

    //TODO: Remove this when GUI filter support is implemented properly.
    public void tempSavePayeeFiltersToDrive() {
        String file = "c:\\EconoStats\\payeeFilters.json";

        String pFilters = null;
        try {
            pFilters = new String(Files.readAllBytes(Paths.get(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<PayeeFilter> payeeFilters = JsonUtils.getJsonElement(PayeeFilter.class, pFilters);

        String convertedPayeeFilters = convertObjectsToJson(payeeFilters, "payeeFilters");

        File filePathTransactions = null;
        try {
            filePathTransactions = saveFileLocally("output/payeeFilters.json", convertedPayeeFilters);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String fileId = getFileId("payeeFilters.json", MimeTypes.APPLICATION_JSON.toString());
            String folderId = getFileId(storagePath, APPLICATION_VND_GOOGLE_APPS_FOLDER);

            if (fileId == null) {
                accountTransactionDao.createFile(filePathTransactions, Arrays.asList(folderId), MimeTypes.APPLICATION_JSON.toString(), MimeTypes.APPLICATION_JSON.toString());
            } else {
                accountTransactionDao.updateFile(fileId, new File("output/payeeFilters.json"), MimeTypes.APPLICATION_JSON.toString());
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}