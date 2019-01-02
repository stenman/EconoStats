package se.perfektum.econostats.spreadsheet;

import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gets AccountTransactions from storage
 * Performs various calculations on transaction values
 * Creates a spreadsheet on monthly payments
 */
public class OdfToolkitSpreadsheetProcessor implements SpreadsheetProcessor {
    private static final int ROW_COUNT = 14;
    private static final int COLUMN_OFFSET = 1;
    private static final int ROUNDING = 0;
    private static final String MONTH = "Month";
    private static final String TOTAL = "Total";
    private static final String AVERAGE = "Average";
    private static final String GRAND_TOTAL = "Grand Total";
    private static final Color GREY = new Color(200, 200, 200);
    private static final Color PASTEL_PEACH = new Color(255, 225, 200);
    private static final Color PASTEL_PINK = new Color(250, 210, 255);
    private static final Color PASTEL_PURPLE = new Color(220, 210, 255);

    //TODO: Create an "anchor" or similar, to be able to move the whole construct anywhere in the sheet.
    //TODO: Fix widths (calculation of this is pretty bad as it is)
    @Override
    public SpreadsheetDocument createSpreadsheet(List<AccountTransaction> accountTransactions, List<PayeeFilter> payeeFilters) throws Exception {
        Map<Year, List<AccountTransaction>> transactionsByYear = accountTransactions.stream().collect(Collectors.groupingBy(d -> Year.of(d.getDate().getYear()), TreeMap::new, Collectors.toList()));

        int i = 0;
        SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
        for (Year year : transactionsByYear.keySet()) {
            List<PayeeFilter> adaptedFilters = adaptPayeeFilters(transactionsByYear.get(year), payeeFilters);
            if (adaptedFilters.size() > 0) {
                doc.appendSheet(year.toString());
                Table sheet = doc.getSheetByIndex(i + 1);

                Collections.sort(adaptedFilters, Comparator.comparing(PayeeFilter::getAlias));

                setHeaders(adaptedFilters, sheet);

                processPayees(transactionsByYear.get(year), adaptedFilters, sheet);

                calcMonthlyTotals(adaptedFilters, sheet);

                // Calculate total average monthly
                calcTotalsPerPayee(adaptedFilters, sheet, 13, "=ROUND(AVERAGE(");

                // Calculate grand total
                calcTotalsPerPayee(adaptedFilters, sheet, 14, "=ROUND(SUM(");

                i++;
            }
        }
        doc.removeSheet(0);
        return doc;
    }

    private List<PayeeFilter> adaptPayeeFilters(List<AccountTransaction> transactions, List<PayeeFilter> filters) {

        Set<String> trans = transactions.stream().map(AccountTransaction::getName).collect(Collectors.toSet());

        Set<PayeeFilter> adaptedFilters = new HashSet<>();
        for (String name : trans) {
            adaptedFilters.addAll(filters.stream().filter(t -> t.getPayees().contains(name)).collect(Collectors.toSet()));
        }
        return new ArrayList<>(adaptedFilters);
    }

    private void processPayees(List<AccountTransaction> accountTransactions, List<PayeeFilter> payeeFilters, Table sheet) {
        for (int i = 0; i < payeeFilters.size(); i++) {
            Map<YearMonth, Integer> amounts = new HashMap<>();

            // Accumulate amounts for each filter, grouped by year and month
            for (AccountTransaction transaction : accountTransactions) {
                if (payeeFilters.get(i).getPayees().stream().anyMatch(transaction.getName()::contains)) {
                    YearMonth ym = YearMonth.of(transaction.getDate().getYear(), transaction.getDate().getMonthValue());
                    amounts.merge(ym, Math.abs(transaction.getAmount() / 100), Integer::sum);
                }
            }

            // Set payee headers
            // Calculate payee invoices
            for (Map.Entry<YearMonth, Integer> entry : amounts.entrySet()) {
                String alias = payeeFilters.get(i).getAlias();
                setCellValues(sheet.getCellByPosition(i + COLUMN_OFFSET, 0), alias, true, PASTEL_PEACH, alias);
                sheet.getCellByPosition(i + COLUMN_OFFSET, entry.getKey().getMonth().getValue())
                        .setDoubleValue((double) Math.abs(entry.getValue().intValue()));
            }

            // Calculate average per payee
            String odfColName = getColumnName(i + COLUMN_OFFSET + 1);
            setCellValues(sheet.getCellByPosition(i + COLUMN_OFFSET, ROW_COUNT - 1), "", true, PASTEL_PINK);
            sheet.getCellByPosition(i + COLUMN_OFFSET, ROW_COUNT - 1).setFormula(String.format("=ROUND(AVERAGE(%s2:%s13);%s)", odfColName, odfColName, ROUNDING));
            // Calculate totals per payee
            setCellValues(sheet.getCellByPosition(i + COLUMN_OFFSET, ROW_COUNT), "", true, PASTEL_PURPLE);
            sheet.getCellByPosition(i + COLUMN_OFFSET, ROW_COUNT).setFormula(String.format("=ROUND(SUM(%s2:%s13);%s)", odfColName, odfColName, ROUNDING));
        }
    }

    private void setHeaders(List<PayeeFilter> payeeFilters, Table sheet) {
        setCellValues(sheet.getCellByPosition(0, 0), MONTH, true, PASTEL_PEACH);
        setCellValues(sheet.getCellByPosition(payeeFilters.size() + COLUMN_OFFSET, 0), TOTAL, true, GREY);
        setCellValues(sheet.getCellByPosition(0, ROW_COUNT - 1), AVERAGE, true, PASTEL_PINK);
        setCellValues(sheet.getCellByPosition(0, ROW_COUNT), GRAND_TOTAL, true, PASTEL_PURPLE);
        createMonthColumn(sheet);
    }

    private void setCellValues(Cell cell, String value, boolean bold) {
        setCellValues(cell, value, bold, null);
    }

    private void setCellValues(Cell cell, String value, boolean bold, Color color) {
        setCellValues(cell, value, bold, color, null);
    }

    private void setCellValues(Cell cell, String value, boolean bold, Color color, String columnAlias) {
        if (columnAlias != null) {
            cell.getTableColumn().setWidth(columnAlias.length() + 15);
        }
        cell.setStringValue(value);
        cell.setCellBackgroundColor(color);
        if (bold) {
            cell.setFont(new Font("", StyleTypeDefinitions.FontStyle.BOLD, 10));
        }
    }

    private void calcTotalsPerPayee(List<PayeeFilter> payeeFilters, Table sheet, int rowIndex, String function) {
        setCellValues(sheet.getCellByPosition(payeeFilters.size() + COLUMN_OFFSET, rowIndex), "", true, GREY);
        sheet.getCellByPosition(payeeFilters.size() + COLUMN_OFFSET, rowIndex).setFormula(String.format(function + "%s2:%s13);%s)", getColumnName(payeeFilters.size() + COLUMN_OFFSET + 1), getColumnName(payeeFilters.size() + COLUMN_OFFSET + 1), ROUNDING));
    }

    private void calcMonthlyTotals(List<PayeeFilter> payeeFilters, Table sheet) {
        for (int i = 2; i < ROW_COUNT; i++) {
            setCellValues(sheet.getCellByPosition(payeeFilters.size() + COLUMN_OFFSET, i - 1), "", true);
            sheet.getCellByPosition(payeeFilters.size() + COLUMN_OFFSET, i - 1).setFormula(String.format("=IF(COUNTBLANK(B%s:%s)=%d;\"\";ROUND(SUM(B%s:%s);%d))", i, getColumnName(payeeFilters.size() + COLUMN_OFFSET) + i, payeeFilters.size(), i, getColumnName(payeeFilters.size() + COLUMN_OFFSET) + i, ROUNDING));
        }
    }

    private void createMonthColumn(Table sheet) {
        setCellValues(sheet.getCellByPosition(0, 1), Month.JANUARY.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH);
        setCellValues(sheet.getCellByPosition(0, 2), Month.FEBRUARY.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH);
        setCellValues(sheet.getCellByPosition(0, 3), Month.MARCH.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH);
        setCellValues(sheet.getCellByPosition(0, 4), Month.APRIL.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH);
        setCellValues(sheet.getCellByPosition(0, 5), Month.MAY.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH);
        setCellValues(sheet.getCellByPosition(0, 6), Month.JUNE.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH);
        setCellValues(sheet.getCellByPosition(0, 7), Month.JULY.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH);
        setCellValues(sheet.getCellByPosition(0, 8), Month.AUGUST.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH);
        setCellValues(sheet.getCellByPosition(0, 9), Month.SEPTEMBER.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH);
        setCellValues(sheet.getCellByPosition(0, 10), Month.OCTOBER.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH);
        setCellValues(sheet.getCellByPosition(0, 11), Month.NOVEMBER.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH);
        setCellValues(sheet.getCellByPosition(0, 12), Month.DECEMBER.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH);
    }

    //TODO: Should be a static class in a Spreadsheet utility class!
    private String getColumnName(int index) {
        String[] result = new String[index];
        String colName;
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

