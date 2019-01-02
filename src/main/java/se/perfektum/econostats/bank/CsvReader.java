package se.perfektum.econostats.bank;

import se.perfektum.econostats.domain.AccountTransaction;

import java.util.List;

/**
 * Parses a CSV file.
 */
public interface CsvReader {

    List<AccountTransaction> parseCsv(String csvFile, String csvSplitBy, char[] charsToEscape) throws Exception;
}
