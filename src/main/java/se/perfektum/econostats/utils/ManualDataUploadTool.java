package se.perfektum.econostats.utils;

import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import se.perfektum.econostats.EconoStats;
import se.perfektum.econostats.bank.CsvReader;
import se.perfektum.econostats.bank.nordea.NordeaCsvReader;
import se.perfektum.econostats.dao.AccountTransactionDao;
import se.perfektum.econostats.dao.googledrive.GoogleDriveDao;
import se.perfektum.econostats.dao.googledrive.MimeTypes;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import static se.perfektum.econostats.dao.googledrive.GoogleDriveDao.APPLICATION_VND_GOOGLE_APPS_FOLDER;

/**
 * Simple tool for uploading/resetting data on Google Drive. Mainly for testing purposes.
 * <p>
 * NOTE: recurringTransactions file will NOT be restored!
 */
public class ManualDataUploadTool {

    private static EconoStats econoStats;

    private final static String storagePath = "EconoStats";
    private final static String localPayeeFilters = "c:\\EconoStats\\payeeFilters.json";
    private final static String localAccountTransactions = "c:/EconoStats/nordeaGemensamt.csv";

    static AccountTransactionDao dao = new GoogleDriveDao();
    static private CsvReader csvReader = new NordeaCsvReader();

    public static void main(String args[]) {
        ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
        econoStats = (EconoStats) context.getBean("econoStats");

        List<PayeeFilter> payeeFilters = savePayeeFiltersToDrive();
        List<AccountTransaction> accountTransactions = saveAccountTransactionsToDrive();
        // Pass accountTransaction=null when doing a full reset
        // Passing a list of accountTransactions here will merge it will an existing list on Drive!
        generateRecurringTransactions(payeeFilters, null);
    }

    private static List<AccountTransaction> saveAccountTransactionsToDrive() {
        List<AccountTransaction> transactions = null;
        try {
            transactions = csvReader.getAccountTransactionsFromFile(localAccountTransactions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveJsonItemsToDrive(transactions, "transactions", "accountTransactions");
        return transactions;
    }

    private static List<PayeeFilter> savePayeeFiltersToDrive() {
        String pFilters = null;
        try {
            pFilters = new String(Files.readAllBytes(Paths.get(localPayeeFilters)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<PayeeFilter> payeeFilters = JsonUtils.getJsonElement(PayeeFilter.class, pFilters);
        saveJsonItemsToDrive(payeeFilters, "payeeFilters", "payeeFilters");
        return payeeFilters;
    }

    private static void generateRecurringTransactions(List<PayeeFilter> payeeFilters, List<AccountTransaction> accountTransactions) {
        try {
            if (payeeFilters != null) {
                econoStats.generateRecurringTransactions(payeeFilters, accountTransactions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveJsonItemsToDrive(List<?> jsonItems, String name, String rootName) {
        String convertedJsonItems = JsonUtils.convertObjectsToJson(jsonItems, rootName);

        // Save file locally first, in order to be able to upload the file to Drive
        File filePathTransactions = null;
        try {
            filePathTransactions = saveFileLocally(String.format("output/%s.json", name), convertedJsonItems);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String folderId = getFileId(storagePath, APPLICATION_VND_GOOGLE_APPS_FOLDER);
            String fileId = getFileId(String.format("%s.json", name), MimeTypes.APPLICATION_JSON.toString());

            if (folderId == null) {
                folderId = dao.createFolder(storagePath);
            }
            if (fileId == null) {
                dao.createFile(filePathTransactions, Arrays.asList(folderId), MimeTypes.APPLICATION_JSON.toString(), MimeTypes.APPLICATION_JSON.toString());
            } else {
                dao.updateFile(fileId, new File(String.format("output/%s.json", name)), MimeTypes.APPLICATION_JSON.toString());
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private static File saveFileLocally(String filePath, String content) throws IOException {
        FileUtils.writeStringToFile(new File(filePath), content, "UTF-8");
        return new File(filePath);
    }

    private static String getFileId(String name, String mimeType) throws IOException, GeneralSecurityException {
        List<String> items = dao.searchForFile(name, mimeType);
        if (items.isEmpty()) {
            return null;
        } else if (items.size() > 1) {
            throw new IOException("Inconsistency in file/folder structure. More than one item found! Please check folder/file structure!");
        }
        return items.get(0);
    }
}
