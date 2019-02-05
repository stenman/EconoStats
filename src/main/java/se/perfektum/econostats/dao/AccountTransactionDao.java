package se.perfektum.econostats.dao;

import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Handles loads and writes to data storage
 */
public interface AccountTransactionDao {
    String createFolder(String name) throws IOException, GeneralSecurityException;

    String createFile(File filePath, List<String> parents, String fileMimeType, String fileContentMimeType) throws IOException, GeneralSecurityException;

    void updateFile(String fileId, File filePath, String fileContentMimeType) throws IOException, GeneralSecurityException;

    List<String> searchForFile(String name, String mimeType) throws IOException, GeneralSecurityException;

    String getFile(String fileId) throws IOException, GeneralSecurityException;

    void saveAccountTransactionsAsJsonString(List<AccountTransaction> accountTransactions);

    void savePayeeFiltersAsJsonString(List<PayeeFilter> payeeFilters);
}
