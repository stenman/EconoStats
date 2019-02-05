package se.perfektum.econostats.dao.googledrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.perfektum.econostats.dao.AccountTransactionDao;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;
import se.perfektum.econostats.utils.JsonUtils;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GoogleDriveDao implements AccountTransactionDao {

    final Logger LOGGER = LoggerFactory.getLogger(GoogleDriveDao.class);

    private static final String APPLICATION_NAME = "EconoStats";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String ACCESS_TYPE = "offline";
    private static final String USER = "user";
    private static final int PORT_NUMBER = 8888;
    private static final String storagePath = "EconoStats";

    public static final String APPLICATION_VND_GOOGLE_APPS_FOLDER = "application/vnd.google-apps.folder";

    /**
     * Global instance of the scopes required by this application.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    @Override
    public String createFolder(String name) throws IOException, GeneralSecurityException {
        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setMimeType(APPLICATION_VND_GOOGLE_APPS_FOLDER);

        LOGGER.debug(String.format("Creating folder on Google Drive: '%s'", name));
        File file = getService().files().create(fileMetadata)
                .setFields("id")
                .execute();
        return file.getId();
    }

    @Override
    public String createFile(java.io.File filePath, List<String> parents, String fileMimeType, String fileContentMimeType) throws IOException, GeneralSecurityException {
        File fileMetadata = new File();
        fileMetadata.setName(filePath.getName());
        fileMetadata.setParents(parents);
        fileMetadata.setMimeType(fileMimeType);

        FileContent mediaContent = new FileContent(fileContentMimeType, filePath);

        LOGGER.debug(String.format("Creating file on Google Drive: filePath:[%s], parents:[%s], fileMimeType:[%s], fileContentMimeType:[%s]", filePath, parents, fileMimeType, fileContentMimeType));
        File file = getService().files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
        return file.getId();
    }

    @Override
    public void updateFile(String fileId, java.io.File filePath, String fileContentMimeType) throws IOException, GeneralSecurityException {
        LOGGER.debug(String.format("Fetching file from Google Drive - fileId:[%s]", fileId));
        File existingFile = getService().files().get(fileId).execute();
        File fileMetadata = new File();
        fileMetadata.setName(existingFile.getName());
        fileMetadata.setParents(existingFile.getParents());
        fileMetadata.setMimeType(existingFile.getMimeType());

        FileContent mediaContent = new FileContent(fileContentMimeType, filePath);

        LOGGER.debug(String.format("Updating file on Google Drive with the following parameters - fileId:[%s], name:[%s], parents:[%s], mimeType:[%s], fileContentMimeType:[%s], filePath:[%s]"
                , fileId, fileMetadata.getName(), fileMetadata.getParents(), fileMetadata.getMimeType(), fileContentMimeType, filePath));
        getService().files().update(fileId, fileMetadata, mediaContent).execute();
    }


    @Override
    public List<String> searchForFile(String name, String mimeType) throws IOException, GeneralSecurityException {
        String pageToken = null;
        LOGGER.debug(String.format("Searching for file on Google Drive - name:'%s', mimeType:'%s'", name, mimeType));
        List<String> items = new ArrayList<>();
        do {
            FileList result = getService().files().list()
                    .setQ("mimeType = '" + mimeType + "' and trashed = false")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name, parents)")
                    .setPageToken(pageToken)
                    .execute();
            items.addAll(result.getFiles().stream().filter(d -> d.getName().equals(name)).map(File::getId).collect(Collectors.toList()));
            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        if (items.isEmpty()) {
            LOGGER.debug(String.format("File not found on Google Drive - name:'%s', mimeType:'%s'", name, mimeType));
        }
        return items;
    }

    @Override
    public String getFile(String fileId) throws IOException, GeneralSecurityException {
        OutputStream outputStream = new ByteArrayOutputStream();
        LOGGER.debug(String.format("Downloading file from Google Drive - fileId:'%s'", fileId));
        getService().files().get(fileId)
                .executeMediaAndDownloadTo(outputStream);
        return outputStream.toString();
    }

    @Override
    public void saveAccountTransactionsAsJsonString(List<AccountTransaction> accountTransactions, boolean overwrite) {
        saveJsonItemsToDrive(accountTransactions, "transactions", "accountTransactions");
    }

    @Override
    public void savePayeeFiltersAsJsonString(List<PayeeFilter> payeeFilters, boolean overwrite) {
        saveJsonItemsToDrive(payeeFilters, "payeeFilters", "payeeFilters");
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        LOGGER.trace(String.format("Loading local client secrets from file '%s'", CREDENTIALS_FILE_PATH));
        InputStream in = GoogleDriveDao.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        LOGGER.trace("Loading Google client secrets");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        LOGGER.trace(String.format("Building Google Authorization Code Flow with the following parameters - SCOPES:[%s], Tokens Directory Path:[%s], Access Type:[%s]", SCOPES, TOKENS_DIRECTORY_PATH, ACCESS_TYPE));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType(ACCESS_TYPE)
                .build();
        LOGGER.trace(String.format("Building Local Server Receiver on port [%s]", PORT_NUMBER));
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(PORT_NUMBER).build();
        LOGGER.trace(String.format("Authorizing user '%s'", USER));
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize(USER);
    }

    private Drive getService() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        LOGGER.trace(String.format("Building authorized Drive API client server for '%s'", APPLICATION_NAME));
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        return service;
    }

    public void saveJsonItemsToDrive(List<?> jsonItems, String name, String rootName) {
        String convertedJsonItems = JsonUtils.convertObjectsToJson(jsonItems, rootName);

        // Save file locally first, in order to be able to upload the file to Drive
        java.io.File filePathTransactions = null;
        try {
            filePathTransactions = saveFileLocally(String.format("output/%s.json", name), convertedJsonItems);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String folderId = getFileId(storagePath, APPLICATION_VND_GOOGLE_APPS_FOLDER);
            String fileId = getFileId(String.format("%s.json", name), MimeTypes.APPLICATION_JSON.toString());

            if (folderId == null) {
                folderId = createFolder(storagePath);
            }
            if (fileId == null) {
                createFile(filePathTransactions, Arrays.asList(folderId), MimeTypes.APPLICATION_JSON.toString(), MimeTypes.APPLICATION_JSON.toString());
            } else {
                updateFile(fileId, new java.io.File(String.format("output/%s.json", name)), MimeTypes.APPLICATION_JSON.toString());
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private java.io.File saveFileLocally(String filePath, String content) throws IOException {
        FileUtils.writeStringToFile(new java.io.File(filePath), content, "UTF-8");
        return new java.io.File(filePath);
    }

    private String getFileId(String name, String mimeType) throws IOException, GeneralSecurityException {
        List<String> items = searchForFile(name, mimeType);
        if (items.isEmpty()) {
            return null;
        } else if (items.size() > 1) {
            throw new IOException("Inconsistency in file/folder structure. More than one item found! Please check folder/file structure!");
        }
        return items.get(0);
    }
}