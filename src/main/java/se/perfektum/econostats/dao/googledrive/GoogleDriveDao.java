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
import se.perfektum.econostats.domain.PayeeFilter;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GoogleDriveDao implements AccountTransactionDao {
    private static final String APPLICATION_NAME = "EconoStats";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    public static final String APPLICATION_VND_GOOGLE_APPS_FOLDER = "application/vnd.google-apps.folder";
    public static final String APPLICATION_VND_GOOGLE_APPS_FILE = "application/json";

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
    public String createFolder(String name) throws IOException, GeneralSecurityException {
        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setMimeType(APPLICATION_VND_GOOGLE_APPS_FOLDER);

        File file = getService().files().create(fileMetadata)
                .setFields("id")
                .execute();
        return file.getId();
    }

    @Override
    public String createFile(List<String> parents) throws IOException, GeneralSecurityException {
        File fileMetadata = new File();
        fileMetadata.setName("transactions.json");
        fileMetadata.setParents(parents);
        fileMetadata.setMimeType(APPLICATION_VND_GOOGLE_APPS_FILE);

        java.io.File filePath = new java.io.File("GoogleDriveSandbox/src/main/resources/transactions.json");
        FileContent mediaContent = new FileContent("application/json", filePath);

        File file = getService().files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
        return file.getId();
    }

    @Override
    public void updateFile(String fileId) throws IOException, GeneralSecurityException {
        File existingFile = getService().files().get(fileId).execute();
        File fileMetadata = new File();
        fileMetadata.setName(existingFile.getName());
        fileMetadata.setParents(existingFile.getParents());
        fileMetadata.setMimeType(existingFile.getMimeType());

        java.io.File filePath = new java.io.File("GoogleDriveSandbox/src/main/resources/transactions.json");
        FileContent mediaContent = new FileContent("application/json", filePath);

        getService().files().update(fileId, fileMetadata, mediaContent).execute();
    }

    @Override
    public List<String> searchForFile(String name, String mimeType) throws IOException, GeneralSecurityException {
        String pageToken = null;
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

        return items;
    }

    @Override
    public String getFile(String fileId) throws IOException, GeneralSecurityException {
        OutputStream outputStream = new ByteArrayOutputStream();
        getService().files().get(fileId)
                .executeMediaAndDownloadTo(outputStream);
        return outputStream.toString();
    }
}