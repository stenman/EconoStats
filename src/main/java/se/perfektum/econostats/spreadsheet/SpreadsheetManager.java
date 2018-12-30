package se.perfektum.econostats.spreadsheet;

import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;

import java.io.IOException;
import java.util.List;

public interface SpreadsheetManager {
    void createNewSpreadsheet(List<AccountTransaction> accountTransactions, List<PayeeFilter> payeeFilters) throws Exception;

    List<PayeeFilter> syncPayeeFilters(List<PayeeFilter> localPayeeFilters, String transactions) throws IOException;

    List<AccountTransaction> mergeAccountTransactions(List<AccountTransaction> importedAccountTransactions, String transactions) throws IOException;
}
