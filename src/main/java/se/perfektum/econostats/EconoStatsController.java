package se.perfektum.econostats;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.perfektum.econostats.bank.CsvReader;
import se.perfektum.econostats.configuration.AppProperties;
import se.perfektum.econostats.dao.AccountTransactionDao;
import se.perfektum.econostats.dao.googledrive.MimeTypes;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;
import se.perfektum.econostats.spreadsheet.SpreadsheetManager;
import se.perfektum.econostats.utils.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static se.perfektum.econostats.dao.googledrive.GoogleDriveDao.APPLICATION_VND_GOOGLE_APPS_FOLDER;
import static se.perfektum.econostats.utils.FileUtils.saveFileLocally;

/**
 * The main class of this application
 */
public class EconoStatsController {
    final Logger LOGGER = LoggerFactory.getLogger(EconoStatsController.class);

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
    private String csvPath;
    private String csvFilePath;

    private static final String ACCOUNT_TRANSACTIONS = "accountTransactions";

    private static ObservableList<se.perfektum.econostats.gui.model.PayeeFilter> payeeFilters = FXCollections.observableArrayList();
    private static ObservableList<AccountTransaction> accountTransactions = FXCollections.observableArrayList();

    public EconoStatsController(SpreadsheetManager spreadsheetManager, CsvReader csvReader, AccountTransactionDao accountTransactionDao, AppProperties appProperties) {
        this.spreadsheetManager = spreadsheetManager;
        this.csvReader = csvReader;
        this.accountTransactionDao = accountTransactionDao;

        initProperties(appProperties);

        List<se.perfektum.econostats.domain.PayeeFilter> pfs = fetchPayeeFilters();
        payeeFilters.addAll(pfs == null ? FXCollections.observableArrayList(Collections.emptyList()) : FXCollections.observableArrayList(se.perfektum.econostats.gui.model.PayeeFilter.convertFromDomain(pfs)));
    }

    public String getCsvPath() {
        return csvPath;
    }

    public String getCsvFilePath() {
        return csvFilePath;
    }

    /**
     * Access method for getting PayeeFilters.
     *
     * @return an observable list of PayeeFilters
     */
    public ObservableList<se.perfektum.econostats.gui.model.PayeeFilter> getPayeeFilters() {
        return payeeFilters;
    }

    /**
     * Access method for getting AccountTransactions.
     *
     * @return an observable list of AccountTransactions
     */
    public ObservableList<AccountTransaction> getAccountTransactions() {
        return accountTransactions;
    }

    /**
     * Access method for setting AccountTransactions.
     */
    public void setAccountTransactions(List<AccountTransaction> accountTransactions) {
        this.accountTransactions.addAll(accountTransactions);
    }

    public List<AccountTransaction> fetchAccountTransactions(String filePath) {
        try {
            return csvReader.getAccountTransactionsFromFile(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<PayeeFilter> fetchPayeeFilters() {
        try {
            String fileId = getFileId(payeeFiltersFileName, MimeTypes.APPLICATION_JSON.toString());
            String filters = fileId == null ? null : accountTransactionDao.getFile(fileId);
            return filters == null ? null : JsonUtils.getJsonElement(PayeeFilter.class, filters);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public void savePayeeFilters(List<PayeeFilter> payeeFilters) {
        accountTransactionDao.savePayeeFiltersAsJsonString(payeeFilters);
    }

    public void generateRecurringTransactions() {
        try {
            generateRecurringTransactions(se.perfektum.econostats.gui.model.PayeeFilter.convertToDomain(payeeFilters), accountTransactions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateRecurringTransactions(List<PayeeFilter> payeeFilters, List<AccountTransaction> accountTransactionsDelta) throws Exception {
        String folderId = getFileId(storagePath, APPLICATION_VND_GOOGLE_APPS_FOLDER);

        payeeFilters = payeeFilters.stream().filter(f -> f.isActive()).collect(Collectors.toList());

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

        if (transactionFileId == null && accountTransactionsDelta != null && !accountTransactionsDelta.isEmpty()) {
            LOGGER.debug(String.format("File '%s' did not exist, no merge needed.", transactionsFilename));

            // save imported transactions locally
            LOGGER.debug(String.format("Storing file '%s' to local disk.", transactionsPath));
            String convertedTransactions = JsonUtils.convertObjectsToJson(accountTransactionsDelta, ACCOUNT_TRANSACTIONS);
            File filePathTransactions = saveFileLocally(transactionsPath, convertedTransactions);

            // save imported transactions to Drive
            accountTransactionDao.createFile(filePathTransactions, Arrays.asList(folderId), MimeTypes.APPLICATION_JSON.toString(), MimeTypes.APPLICATION_JSON.toString());

            // create spreadsheet
            File filePathSpreadsheet = spreadsheetManager.createNewSpreadsheet(recurringTransactionsPath, accountTransactionsDelta, payeeFilters);

            // save/update spreadsheet to Drive
            if (spreadsheetFileId == null) {
                accountTransactionDao.createFile(filePathSpreadsheet, Arrays.asList(folderId), MimeTypes.GOOGLE_API_SPREADSHEET.toString(), MimeTypes.TEXT_ODS.toString());
            } else {
                accountTransactionDao.updateFile(spreadsheetFileId, new File(recurringTransactionsPath), MimeTypes.TEXT_ODS.toString());
            }
        } else {
            // get transactions from Drive
            String transactions = accountTransactionDao.getFile(transactionFileId);

            List<AccountTransaction> accountTransactions;
            // merge transactions with imported transactions
            if (accountTransactionsDelta != null && !accountTransactionsDelta.isEmpty()) {
                accountTransactions = spreadsheetManager.mergeAccountTransactions(accountTransactionsDelta, transactions);
                // save merged transactions locally
                String convertedTransactions = JsonUtils.convertObjectsToJson(accountTransactions, ACCOUNT_TRANSACTIONS);
                FileUtils.writeStringToFile(new java.io.File(transactionsPath), convertedTransactions, "UTF-8");

                // overwrite transaction file on Drive
                accountTransactionDao.updateFile(transactionFileId, new File(transactionsPath), MimeTypes.APPLICATION_JSON.toString());
            } else {
                accountTransactions = JsonUtils.getJsonElement(AccountTransaction.class, transactions);
            }

            // create spreadsheet
            File filePathSpreadsheet = spreadsheetManager.createNewSpreadsheet(recurringTransactionsPath, accountTransactions, payeeFilters);

            // save/update spreadsheet to Drive
            if (spreadsheetFileId == null) {
                accountTransactionDao.createFile(filePathSpreadsheet, Arrays.asList(folderId), MimeTypes.GOOGLE_API_SPREADSHEET.toString(), MimeTypes.TEXT_ODS.toString());
            } else {
                accountTransactionDao.updateFile(spreadsheetFileId, new File(recurringTransactionsPath), MimeTypes.TEXT_ODS.toString());
            }
        }
    }

    /**
     * Searches for a file in storage.
     */
    //TODO: make optional
    //TODO: This is Google Drive, not general. Make general.
    //TODO: Logs and exceptions should probably be moved to storage area
    public String getFileId(String name, String mimeType) {
        try {
            List<String> items;
            items = accountTransactionDao.searchForFile(name, mimeType);

            if (items.isEmpty()) {
                LOGGER.debug(String.format("Could not find file '%s' in storage", name));
                return null;
//            } else if (items.) {
//                LOGGER.debug(String.format("Could not find file '%s' in storage", name));
//                return null;
            } else if (items.size() > 1) {
                LOGGER.error("Inconsistency in file/folder structure. More than one item found! Please check folder/file structure!");
                throw new IOException("Inconsistency in file/folder structure. More than one item found! Please check folder/file structure!");
            }
            return items.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initProperties(AppProperties appProperties) {
        localFilesPath = appProperties.getOutputFilesPath();
        transactionsFilename = appProperties.getTransactionsFilename();
        spreadsheetFilename = appProperties.getSpreadsheetFilename();
        storagePath = appProperties.getStoragePath();
        transactionsPath = appProperties.getTransactionsPath();
        recurringTransactionsPath = appProperties.getRecurringTransactionsPath();
        payeeFiltersFileName = appProperties.getPayeeFiltersFileName();
        csvPath = appProperties.getCsvPath();
        csvFilePath = appProperties.getCsvFilePath();
    }
}