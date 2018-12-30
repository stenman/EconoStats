package se.perfektum.econostats.spreadsheet;

import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;

import java.util.List;

public interface SpreadsheetManager {
    void createNewSpreadsheet(List<AccountTransaction> accountTransactions, List<PayeeFilter> payeeFilters) throws Exception;

    List<AccountTransaction> mergeAccountTransactions(List<AccountTransaction> importedAccountTransactions, String transactions);
}
