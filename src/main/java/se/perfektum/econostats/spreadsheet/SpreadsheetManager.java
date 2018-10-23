package se.perfektum.econostats.spreadsheet;

import org.jopendocument.dom.OOUtils;
import org.odftoolkit.simple.SpreadsheetDocument;
import se.perfektum.econostats.domain.PayeeFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//TODO: This class needs to be refactored (and renamed)!

/**
 * A wrapper class that simply manages spreadsheet creation and saving the produced spreadsheet file.
 */
public class SpreadsheetManager implements ISpreadsheetManager {

    private ISpreadsheetProcessor spreadsheetProcessor;

    public SpreadsheetManager(SpreadsheetProcessor spreadsheetProcessor) {
        this.spreadsheetProcessor = spreadsheetProcessor;
    }

    @Override
    public void manageSpreadsheet() throws Exception {

        //TODO: Create and get the config
        List<PayeeFilter> payeesConfigs = new ArrayList<>();

        SpreadsheetDocument doc = spreadsheetProcessor.createSpreadsheet(payeesConfigs);

        //TODO: path should be configurable!
        final File file = new File("c:/temp/testdata/simpleodf.ods");
        doc.save(file);
        OOUtils.open(file); //TODO: remove this. exists only for testing purposes

    }
}
