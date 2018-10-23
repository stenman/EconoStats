package se.perfektum.econostats.spreadsheet;

import org.odftoolkit.simple.SpreadsheetDocument;
import se.perfektum.econostats.domain.PayeeConfig;

import java.util.List;

public interface ISpreadsheetProcessor {
    SpreadsheetDocument createSpreadsheet(List<PayeeConfig> payeesConfigs) throws Exception;
}
