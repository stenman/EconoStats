package se.perfektum.econostats.bank;

import se.perfektum.econostats.domain.AccountTransaction;

import java.util.List;

public interface ICsvReader {

    List<AccountTransaction> parseCsv(String csvFile, String csvSplitBy, char[] charsToEscape) throws Exception;
}
