package se.perfektum.econostats.spreadsheet;

import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * Gets AccountTransactions from storage
 * Performs various calculations on transaction values
 * Creates a spreadsheet on monthly payments
 */
public class OdfToolkitSpreadsheetProcessor implements SpreadsheetProcessor {
    private static final int ROW_COUNT = 14;
    private static final int COLUMN_OFFSET = 1;
    private static final String MONTH = "Month";
    private static final String TOTAL = "Total";
    private static final String AVERAGE = "Average";
    private static final String GRAND_TOTAL = "Grand Total";

    @Override
    public SpreadsheetDocument createSpreadsheet(List<AccountTransaction> accountTransactions, List<PayeeFilter> payeeFilters) throws Exception {
        SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
        Table sheet = doc.getSheetByIndex(0);

        sheet.getCellByPosition(0, 0).setStringValue(MONTH);
        sheet.getCellByPosition(payeeFilters.size() + COLUMN_OFFSET, 0).setStringValue(TOTAL);
        sheet.getCellByPosition(0, ROW_COUNT - 1).setStringValue(AVERAGE);
        sheet.getCellByPosition(0, ROW_COUNT - 1).setCellBackgroundColor(new Color(40, 40, 140));
        sheet.getCellByPosition(0, ROW_COUNT).setStringValue(GRAND_TOTAL);
        createMonthColumn(sheet);

        //TODO: Create an "anchor" or similar, to be able to move the whole construct anywhere in the sheet.
        //TODO: Set widths accordingly
        //TODO: Set background colors accordingly
        //TODO: Set font styles (Bold etc) accordingly
        //TODO: Logging
        //TODO: I18N
        // Calculate payee invoices
        for (int i = 0; i < payeeFilters.size(); i++) {
            for (AccountTransaction transaction : accountTransactions) {
                if (transaction.getName().contains(payeeFilters.get(i).getPayeeName())) {
                    //TODO: This is possibly set multiple times, see if there's a way to fix that...
                    sheet.getCellByPosition(i + COLUMN_OFFSET, 0)
                            .setStringValue(payeeFilters.get(i).getAlias());
                    sheet.getCellByPosition(i + COLUMN_OFFSET, transaction.getDate().getMonthValue())
                            .setDoubleValue((double) Math.abs(transaction.getAmount() / 100));
                }
            }
            // Calculate average per payee
            String odfColName = getColumnName(i + COLUMN_OFFSET + 1);
            sheet.getCellByPosition(i + COLUMN_OFFSET, ROW_COUNT - 1)
                    .setFormula("=AVERAGE(" + odfColName + "2:" + odfColName + "13)");
            // Calculate totals per payee
            sheet.getCellByPosition(i + COLUMN_OFFSET, ROW_COUNT)
                    .setFormula("=SUM(" + odfColName + "2:" + odfColName + "13)");
        }

        // Calculate monthly totals for all payees
        calcMonthlyTotals(payeeFilters, sheet);

        // Calculate total average monthly
        calcTotals(payeeFilters, sheet, 13, "=AVERAGE(");

        // Calculate grand total
        calcTotals(payeeFilters, sheet, 14, "=SUM(");

        return doc;
    }

    private void calcTotals(List<PayeeFilter> payeeFilters, Table sheet, int rowIndex, String function) {
        sheet.getCellByPosition(payeeFilters.size() + COLUMN_OFFSET, rowIndex)
                .setFormula(function + getColumnName(payeeFilters.size() + COLUMN_OFFSET + 1) + "2:" + getColumnName(payeeFilters.size() + COLUMN_OFFSET + 1) + "13)");
    }

    private void calcMonthlyTotals(List<PayeeFilter> payeeFilters, Table sheet) {
        for (int i = 2; i < ROW_COUNT; i++) {
            sheet.getCellByPosition(payeeFilters.size() + COLUMN_OFFSET, i - 1)
                    .setFormula("=SUM(B" + i + ":" + getColumnName(payeeFilters.size() + COLUMN_OFFSET) + i + ")");
        }
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

    //TODO: Should be a static class in a Spreadsheet utility class!
    private String getColumnName(int index) {
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

