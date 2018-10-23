package se.perfektum.econostats.spreadsheet;

import org.odftoolkit.simple.SpreadsheetDocument;
import se.perfektum.econostats.domain.PayeeFilter;

import java.util.List;

public interface ISpreadsheetProcessor {
    SpreadsheetDocument createSpreadsheet(List<PayeeFilter> payeesConfigs) throws Exception;
}
