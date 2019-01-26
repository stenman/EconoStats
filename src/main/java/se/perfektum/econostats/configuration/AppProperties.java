package se.perfektum.econostats.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app")
public class AppProperties {
    private String outputFilesPath;
    private String transactionsFilename;
    private String spreadsheetFilename;
    private String storagePath;
    private String transactionsPath;
    private String recurringTransactionsPath;
    private String payeeFiltersFileName;

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

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
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

    public String getPayeeFiltersFileName() {
        return payeeFiltersFileName;
    }

    public void setPayeeFiltersFileName(String payeeFiltersFileName) {
        this.payeeFiltersFileName = payeeFiltersFileName;
    }

}