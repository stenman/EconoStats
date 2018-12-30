package se.perfektum.econostats.spreadsheet;

import org.odftoolkit.simple.SpreadsheetDocument;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//TODO: This class needs to be refactored (and renamed)!

/**
 * A wrapper class that simply manages spreadsheet creation and saving the produced spreadsheet file.
 */
public class OdfToolkitSpreadsheetManager implements SpreadsheetManager {

    private SpreadsheetProcessor spreadsheetProcessor;

    public OdfToolkitSpreadsheetManager(SpreadsheetProcessor spreadsheetProcessor) {
        this.spreadsheetProcessor = spreadsheetProcessor;
    }

    @Override
    public void createSpreadsheet() throws Exception {

        //TODO: Get, and handle PayeeFilters
        //TODO: Merge
        List<AccountTransaction> accountTransactions = new ArrayList<>();
        List<PayeeFilter> payeeFilters = new ArrayList<>();

        // *** NOTE: exclude from odf: all objects that do not exist in premade configuration list of names ***

        SpreadsheetDocument doc = spreadsheetProcessor.createSpreadsheet(accountTransactions, payeeFilters);

        //TODO: path should be configurable!
        final File file = new File("c:/temp/testdata/simpleodf.ods");
        doc.save(file);
//        OOUtils.open(file); //TODO: find another way of opening odf files locally

    }
}
