package se.perfektum.econostats.spreadsheet;

import org.odftoolkit.simple.SpreadsheetDocument;
import se.perfektum.econostats.domain.AccountTransaction;

import java.util.List;

public interface ISpreadsheetProcessor {
    SpreadsheetDocument createSpreadsheet(List<AccountTransaction> payees, List<String> payeesConfigs) throws Exception;
}
