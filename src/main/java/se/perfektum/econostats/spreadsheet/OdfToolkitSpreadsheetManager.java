package se.perfektum.econostats.spreadsheet;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;
import se.perfektum.econostats.utils.JsonUtils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A wrapper class that simply manages spreadsheet creation and saving the produced spreadsheet file.
 */
public class OdfToolkitSpreadsheetManager implements SpreadsheetManager {
    final Logger LOGGER = LoggerFactory.getLogger(OdfToolkitSpreadsheetManager.class);

    private SpreadsheetProcessor spreadsheetProcessor;

    public OdfToolkitSpreadsheetManager(SpreadsheetProcessor spreadsheetProcessor) {
        this.spreadsheetProcessor = spreadsheetProcessor;
    }

    @Override
    public File createNewSpreadsheet(String filePath, List<AccountTransaction> accountTransactions, List<PayeeFilter> payeeFilters) throws Exception {
        SpreadsheetDocument doc = spreadsheetProcessor.createSpreadsheet(accountTransactions, payeeFilters);

        final File file = new File(filePath);
        doc.save(file);
        return file;
    }

    @Override
    public List<AccountTransaction> mergeAccountTransactions(List<AccountTransaction> importedAccountTransactions, String transactions) {
        List<AccountTransaction> dataStoreAccountTransactions = JsonUtils.getJsonElement(AccountTransaction.class, transactions);

        // Hard to solve!
        // Eg. If we have three new transactions, where two are identical (1. deposit 10, 2. withdraw 10, 3. deposit 10), one of the deposits
        // will disappear due to distinction. For now I'm just throwing out a warning.
        // Not sure how to solve this, as there are no IDs on transactions. This scenario is likely, and the two deposits are exactly the same.
        // TODO: Should probably show a warning message (Continue Yes/No) here when GUI is implemented
        if (importedAccountTransactions.size() != importedAccountTransactions.stream().distinct().collect(Collectors.toList()).size()) {
            LOGGER.warn("Imported transactions contains one or more duplicate transactions. This will result in loss of as least one transaction (by distinction)! " +
                    "This may occur if there are eg. two deposits and one withdrawal with the exact same amount on the same day. " +
                    "BE ADVISED that this might yield erroneous results! Please check your imported file!");
        }

        List<AccountTransaction> result = Stream.concat(importedAccountTransactions.stream(), dataStoreAccountTransactions.stream()).distinct().collect(Collectors.toList());

        return result;
    }
}
