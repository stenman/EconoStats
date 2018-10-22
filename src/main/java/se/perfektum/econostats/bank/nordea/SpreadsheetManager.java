package se.perfektum.econostats.bank.nordea;

import org.jopendocument.dom.OOUtils;
import org.odftoolkit.simple.SpreadsheetDocument;
import se.perfektum.econostats.domain.AccountTransaction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//TODO: This class needs to be refactored (and renamed)!

/**
 * A wrapper class that simply manages spreadsheet creation and saving the produced spreadsheet file.
 */
public class SpreadsheetManager {

    public void manageSpreadsheet() throws Exception {

        SpreadsheetMaker spreadsheetMaker = new SpreadsheetMaker();

        List<String> payeesConfigs = new ArrayList<>();
        List<AccountTransaction> payees = new ArrayList<>();

        SpreadsheetDocument doc = spreadsheetMaker.makeSpreadsheet(payees, payeesConfigs);

        //TODO: path should be configurable!
        final File file = new File("c:/temp/testdata/simpleodf.ods");
        doc.save(file);
        OOUtils.open(file); //TODO: remove this. exists only for testing purposes

    }
}
