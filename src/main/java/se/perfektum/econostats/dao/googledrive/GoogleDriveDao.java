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
import se.perfektum.econostats.dao.AccountTransactionDao;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleDriveDao implements AccountTransactionDao {
    private static final String APPLICATION_NAME = "EconoStats";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleDriveDao.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public Drive getService() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        return service;
    }

    @Override
    public String searchFile(String name) throws IOException, GeneralSecurityException {
        String queryParam = "modifiedTime > '2012-06-04T12:00:00' and (mimeType contains 'json') and trashed = false";
        com.google.api.services.drive.Drive.Files.List qry = getService().files().list().setFields("files(id, name)").setQ(queryParam);
        com.google.api.services.drive.model.FileList gLst = qry.execute();
        for (com.google.api.services.drive.model.File gFl : gLst.getFiles()) {
            System.out.println("ID==>" + gFl.getId() + "    Name: " + gFl.getName());
        }
        return null;
    }

    @Override
    public List<AccountTransaction> getAccountTransactions() throws IOException, GeneralSecurityException {
        String pageToken = null;
        do {
            FileList result = getService().files().list()
                    .setQ("'root' in parents and mimeType != 'application/vnd.google-apps.folder' and trashed = false")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name, parents)")
                    .setPageToken(pageToken)
                    .execute();
            for (File file : result.getFiles()) {
                System.out.printf("Found file: %s (%s)\n",
                        file.getName(), file.getId());
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        return null;
    }

    @Override
    public String createFolder(String name) throws IOException, GeneralSecurityException {
        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        File file = getService().files().create(fileMetadata)
                .setFields("id")
                .execute();
        System.out.println("Folder ID: " + file.getId());
        return file.getId();
    }

    @Override
    public String storeAccountTransactions() throws IOException, GeneralSecurityException {
        File fileMetadata = new File();
        fileMetadata.setName("sample.json");
        fileMetadata.setMimeType("application/json");
        java.io.File filePath = new java.io.File("src/main/resources/sample.json");
        FileContent mediaContent = new FileContent("application/json", filePath);
        File file = getService().files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
        return file.getId();
    }

    @Override
    public String storeAccountTransactions(String fileId, File file) throws IOException, GeneralSecurityException {
        File fileMetadata = new File();
        fileMetadata.setName("sample.json");
        fileMetadata.setMimeType("application/json");
        java.io.File filePath = new java.io.File("src/main/resources/sample.json");
        FileContent mediaContent = new FileContent("application/json", filePath);
        File updatedFile = getService().files().update(fileId, file, mediaContent).execute();
        return updatedFile.getId();
    }

    @Override
    public List<PayeeFilter> getPayeeFilter() {
        return null;
    }
}