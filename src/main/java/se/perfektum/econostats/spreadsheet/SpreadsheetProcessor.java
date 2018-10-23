package se.perfektum.econostats.spreadsheet;

import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;
import se.perfektum.econostats.dao.IAccountTransactionDao;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

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
    public SpreadsheetDocument createSpreadsheet(List<PayeeFilter> payeesConfigs) throws Exception {

        List<PayeeFilter> payeeConfig = accountTransactionDao.loadPayeeConfig();
        List<AccountTransaction> payees = accountTransactionDao.loadAccountTransactions();

        SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
        Table sheet = doc.getSheetByIndex(0);

        sheet.getCellByPosition(0, 0).setStringValue(MONTH);

        // Payee - should be dynamic (from user input/config file)
        sheet.getCellByPosition(1, 0).setStringValue("Parkering");
        sheet.getCellByPosition(2, 0).setStringValue("Netflix");

        sheet.getCellByPosition(0, 1).setStringValue(Month.JANUARY.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        sheet.getCellByPosition(0, 2).setStringValue(Month.FEBRUARY.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        sheet.getCellByPosition(0, 3).setStringValue(Month.MARCH.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        sheet.getCellByPosition(0, 4).setStringValue(Month.APRIL.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        sheet.getCellByPosition(0, 5).setStringValue(Month.MAY.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        sheet.getCellByPosition(0, 6).setStringValue(Month.JUNE.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        sheet.getCellByPosition(0, 7).setStringValue(Month.JULY.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        sheet.getCellByPosition(0, 8).setStringValue(Month.AUGUST.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        sheet.getCellByPosition(0, 9).setStringValue(Month.SEPTEMBER.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        sheet.getCellByPosition(0, 10).setStringValue(Month.OCTOBER.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        sheet.getCellByPosition(0, 11).setStringValue(Month.NOVEMBER.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        sheet.getCellByPosition(0, 12).setStringValue(Month.DECEMBER.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));

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

