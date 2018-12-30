package se.perfektum.econostats.spreadsheet;

import org.odftoolkit.simple.SpreadsheetDocument;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;

import java.lang.reflect.AccessibleObject;
import java.util.List;

public interface SpreadsheetProcessor {
    SpreadsheetDocument createSpreadsheet(List<AccountTransaction> accountTransactions, List<PayeeFilter> payeesFilters) throws Exception;
}
