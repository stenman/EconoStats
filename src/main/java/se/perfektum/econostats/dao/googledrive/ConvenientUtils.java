package se.perfektum.econostats.dao.googledrive;

import org.apache.commons.io.FileUtils;
import se.perfektum.econostats.dao.AccountTransactionDao;
import se.perfektum.econostats.utils.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import static se.perfektum.econostats.dao.googledrive.GoogleDriveDao.APPLICATION_VND_GOOGLE_APPS_FOLDER;

public class ConvenientUtils {

    private AccountTransactionDao accountTransactionDao;
    private final static String storagePath = "EconoStats";

    public ConvenientUtils(AccountTransactionDao accountTransactionDao) {
        this.accountTransactionDao = accountTransactionDao;
    }

    public void saveJsonItemsToDrive(List<?> jsonItems, String name, String rootName) {
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
                folderId = accountTransactionDao.createFolder(storagePath);
            }
            if (fileId == null) {
                accountTransactionDao.createFile(filePathTransactions, Arrays.asList(folderId), MimeTypes.APPLICATION_JSON.toString(), MimeTypes.APPLICATION_JSON.toString());
            } else {
                accountTransactionDao.updateFile(fileId, new File(String.format("output/%s.json", name)), MimeTypes.APPLICATION_JSON.toString());
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private File saveFileLocally(String filePath, String content) throws IOException {
        FileUtils.writeStringToFile(new File(filePath), content, "UTF-8");
        return new File(filePath);
    }

    private String getFileId(String name, String mimeType) throws IOException, GeneralSecurityException {
        List<String> items = accountTransactionDao.searchForFile(name, mimeType);
        if (items.isEmpty()) {
            return null;
        } else if (items.size() > 1) {
            throw new IOException("Inconsistency in file/folder structure. More than one item found! Please check folder/file structure!");
        }
        return items.get(0);
    }
}
