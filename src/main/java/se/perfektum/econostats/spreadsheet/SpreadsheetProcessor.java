package se.perfektum.econostats.spreadsheet;

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

    private static final int ROW_COUNT = 14;
    private static final int COLUMN_OFFSET = 1;
    private static final String MONTH = "Month";
    private static final String TOTAL = "Total";

    @Override
    public SpreadsheetDocument createSpreadsheet(List<PayeeFilter> payeesConfigs) throws Exception {

        List<PayeeFilter> payeeConfig = accountTransactionDao.loadPayeeFilter();
        List<AccountTransaction> transactions = accountTransactionDao.loadAccountTransactions();

        SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
        Table sheet = doc.getSheetByIndex(0);

        sheet.getCellByPosition(0, 0).setStringValue(MONTH);
        sheet.getCellByPosition(payeeConfig.size() + COLUMN_OFFSET, 0).setStringValue(TOTAL);
        createMonthColumn(sheet);

        // Payee - should be dynamic (from user input/config file)
        for (int i = 0; i < payeeConfig.size(); i++) {
            for (AccountTransaction transaction : transactions) {
                if (transaction.getName().contains(payeeConfig.get(i).getPayee())) {
                    //TODO: This is possibly set multiple times, see if there's a way to fix that...
                    sheet.getCellByPosition(i + COLUMN_OFFSET, 0).setStringValue(payeeConfig.get(i).getAlias());
                    //TODO: Make ABSolute value (we just want positive numbers here)
                    sheet.getCellByPosition(i + COLUMN_OFFSET, transaction.getDate().getMonthValue()).setDoubleValue(new Double(transaction.getAmount() / 100));
                }
            }
            // Calculate average and totals by payee
            String odfColName = getColumnName(i + COLUMN_OFFSET + 1);
            sheet.getCellByPosition(i + COLUMN_OFFSET, ROW_COUNT - 1).setFormula("=AVERAGE(" + odfColName + "2:" + odfColName + "13)");
            sheet.getCellByPosition(i + COLUMN_OFFSET, ROW_COUNT).setFormula("=SUM(" + odfColName + "2:" + odfColName + "13)");
        }

        // Monthly totals, counting all payees
        for (int i = 1; i < ROW_COUNT; i++) {
            sheet.getCellByPosition(payeeConfig.size() + COLUMN_OFFSET, i).setFormula("SUM(B" + i + ":" + getColumnName(payeeConfig.size() + COLUMN_OFFSET) + i + ")");
        }

        // Grand total
        sheet.getCellByPosition(3, 4).setFormula("D2+D3+D4");

        return doc;
    }

    private void createMonthColumn(Table sheet) {
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
    }

    public static String getColumnName(int index) {
        String[] result = new String[index];
        String colName = "";
        for (int i = 0; i < index; i++) {
            char c = (char) ('A' + (i % 26));
            colName = c + "";
            if (i > 25) {
                colName = result[(i / 26) - 1] + "" + c;
            }
            result[i] = colName;
        }
        return result[result.length - 1];
    }

}

