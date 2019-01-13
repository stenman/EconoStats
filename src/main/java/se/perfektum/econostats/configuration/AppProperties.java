package se.perfektum.econostats.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app")
public class AppProperties {
    private String outputFilesPath;
    private String transactionsFilename;
    private String spreadsheetFilename;
    private String googleDriveFolderName;
    private String transactionsPath;
    private String recurringTransactionsPath;
    private String payeeFiltersFilePath;

    public String getOutputFilesPath() {
        return outputFilesPath;
    }

    public void setOutputFilesPath(String outputFilesPath) {
        this.outputFilesPath = outputFilesPath;
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

    public String getPayeeFiltersFilePath() {
        return payeeFiltersFilePath;
    }

    public void setPayeeFiltersFilePath(String payeeFiltersFilePath) {
        this.payeeFiltersFilePath = payeeFiltersFilePath;
    }

}