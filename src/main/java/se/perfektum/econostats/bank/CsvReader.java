package se.perfektum.econostats.bank;

import se.perfektum.econostats.domain.AccountTransaction;

import java.util.List;

/**
 * Parses a CSV file.
 */
public interface CsvReader {
    List<AccountTransaction> getAccountTransactionsFromFile(String filePath) throws Exception;
}
