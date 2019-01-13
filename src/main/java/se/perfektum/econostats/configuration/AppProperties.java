package se.perfektum.econostats.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app")
public class AppProperties {
    private String localFilesPath;
    private String transactionsFilename;
    private String spreadsheetFilename;
    private String googleDriveFolderName;
    private String transactionsPath;
    private String recurringTransactionsPath;

    public String getLocalFilesPath() {
        return localFilesPath;
    }

    public void setLocalFilesPath(String localFilesPath) {
        this.localFilesPath = localFilesPath;
    }

    public String getTransactionsFilename() {
        return transactionsFilename;
    }

    public void setTransactionsFilename(String transactionsFilename) {
        this.transactionsFilename = transactionsFilename;
    }

    public String getSpreadsheetFilename() {
        return spreadsheetFilename;
    }

    public void setSpreadsheetFilename(String spreadsheetFilename) {
        this.spreadsheetFilename = spreadsheetFilename;
    }

    public String getGoogleDriveFolderName() {
        return googleDriveFolderName;
    }

    public void setGoogleDriveFolderName(String googleDriveFolderName) {
        this.googleDriveFolderName = googleDriveFolderName;
    }

    public String getTransactionsPath() {
        return transactionsPath;
    }

    public void setTransactionsPath(String transactionsPath) {
        this.transactionsPath = transactionsPath;
    }

    public String getRecurringTransactionsPath() {
        return recurringTransactionsPath;
    }

    public void setRecurringTransactionsPath(String recurringTransactionsPath) {
        this.recurringTransactionsPath = recurringTransactionsPath;
    }
}
