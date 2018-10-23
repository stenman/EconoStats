package se.perfektum.econostats.spreadsheet;

import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;
import se.perfektum.econostats.dao.IAccountTransactionDao;
import se.perfektum.econostats.domain.AccountTransaction;

import java.util.List;

//TODO: This class needs to be refactored (and renamed)!

/**
 * Creates a spreadsheet, fills the spreadsheet, does various calculations on spreadsheet values
 */
public class SpreadsheetProcessor implements ISpreadsheetProcessor {

    //TODO: This framework could work, but can you calculate on "empty" cells (ie. not 0 but empty)?

    private IAccountTransactionDao accountTransactionDao;

    public SpreadsheetProcessor(IAccountTransactionDao accountTransactionDao) {
        this.accountTransactionDao = accountTransactionDao;
    }

    private static final String MONTH = "Month";

    @Override
    public SpreadsheetDocument createSpreadsheet(List<String> payeesConfigs) throws Exception {

        List<AccountTransaction> payees = accountTransactionDao.loadAccountTransactions();

        SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
        Table sheet = doc.getSheetByIndex(0);

        // Payee - should be dynamic (from user input/config file)
        sheet.getCellByPosition(0, 0).setStringValue("Month");
        sheet.getCellByPosition(1, 0).setStringValue("Parkering");
        sheet.getCellByPosition(2, 0).setStringValue("Netflix");

        // These should be static
        sheet.getCellByPosition(0, 1).setStringValue("Jan");
        sheet.getCellByPosition(0, 2).setStringValue("Feb");
        sheet.getCellByPosition(0, 3).setStringValue("Mar");

        // Payments
        sheet.getCellByPosition(1, 1).setDoubleValue(23.0);
        sheet.getCellByPosition(1, 1).setCellBackgroundColor(new Color(240, 190, 130));
        sheet.getCellByPosition(1, 2).setDoubleValue(133.0);
        sheet.getCellByPosition(1, 2).setCellBackgroundColor(new Color(240, 190, 130));
        sheet.getCellByPosition(1, 3).setDoubleValue(0.0); //must be able to have "empty" here, else some averages wont work properly
        sheet.getCellByPosition(1, 3).setCellBackgroundColor(new Color(240, 190, 130));

        sheet.getCellByPosition(2, 1).setDoubleValue(109.0);
        sheet.getCellByPosition(2, 2).setDoubleValue(109.0);
        sheet.getCellByPosition(2, 3).setDoubleValue(109.0);

        // Payee average
        sheet.getCellByPosition(1, 4).setFormula("(B2+B3+B4)/3");
        sheet.getCellByPosition(2, 4).setFormula("(C2+C3+C4)/3");

        // Month total
        sheet.getCellByPosition(3, 1).setFormula("B2+C2");
        sheet.getCellByPosition(3, 2).setFormula("B3+C3");
        sheet.getCellByPosition(3, 3).setFormula("B4+C4");

        // Grand total
        sheet.getCellByPosition(3, 4).setFormula("D2+D3+D4");

        return doc;
    }
}

