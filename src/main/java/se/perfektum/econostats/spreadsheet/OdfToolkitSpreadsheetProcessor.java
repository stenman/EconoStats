package se.perfektum.econostats.spreadsheet;

import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gets AccountTransactions from storage Performs various calculations on transaction values Creates a spreadsheet on monthly payments
 */
public class OdfToolkitSpreadsheetProcessor implements SpreadsheetProcessor {
    final Logger LOGGER = LoggerFactory.getLogger(OdfToolkitSpreadsheetProcessor.class);
    private static final int HEADER_DEFAULT_SIZE = 9;
    private static final int COLUMN_WIDTH_MODIFIER = 12;
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

    // TODO: Refactor out all parts that processes AccountTransactions and
    // PayeeFilters, as these don't really qualify as OdfToolkit specifics
    // TODO: Create an "anchor" or similar, to be able to move the whole construct
    // anywhere in the sheet.
    // TODO: Fix widths (calculation of this is pretty bad as it is)
    // TODO: For some reason, appending a new sheet creates 5 columns from the
    // start... can this be fixed?
    // TODO: When opening the spreadsheet in drive, the active sheet is the earliest
    // year. Can this be changed (to the latest year)?
    @Override
    public SpreadsheetDocument createSpreadsheet(List<AccountTransaction> accountTransactions, List<PayeeFilter> payeeFilters) throws Exception {
        List<AccountTransaction> excludedPayees = excludedPayees(accountTransactions, payeeFilters);

        Map<Year, List<AccountTransaction>> transactionsByYear = excludedPayees.stream().collect(Collectors.groupingBy(d -> Year.of(d.getDate().getYear()), TreeMap::new, Collectors.toList()));

        int i = 0;
        SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
        doc.removeSheet(0);
        for (Year year : transactionsByYear.keySet()) {
            List<PayeeFilter> adaptedFilters = adaptPayeeFilters(transactionsByYear.get(year), payeeFilters);
            if (adaptedFilters.size() > 0) {

                doc.appendSheet(year.toString());
                Table sheet = doc.getSheetByIndex(i);

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
        return doc;
    }

    private List<AccountTransaction> excludedPayees(List<AccountTransaction> transactions, List<PayeeFilter> filters) {
        for (PayeeFilter filter : filters) {
            if (filter.getExcludedPayees() != null) {
                for (String exclude : filter.getExcludedPayees()) {
                    transactions.removeIf(t -> t.getHeader().equals(exclude));
                    LOGGER.info(String.format("Removing transaction '%s' before creating spreadsheet", exclude));
                }
            }
        }
        return transactions;
    }

    private List<PayeeFilter> adaptPayeeFilters(List<AccountTransaction> transactions, List<PayeeFilter> filters) {

        Set<String> trans = transactions.stream().map(AccountTransaction::getHeader).collect(Collectors.toSet());

        Set<PayeeFilter> adaptedFilters = new HashSet<>();
        for (PayeeFilter filter : filters) {
            for (String payee : filter.getPayees()) {
                if (trans.stream().anyMatch(t -> t.toLowerCase().contains(payee.toLowerCase()))) {
                    adaptedFilters.add(filter);
                    break;
                }
            }
        }
        return new ArrayList<>(adaptedFilters);
    }

    private void processPayees(List<AccountTransaction> accountTransactions, List<PayeeFilter> payeeFilters, Table sheet) {
        List<AccountTransaction> added = new ArrayList<>();
        for (int i = 0; i < payeeFilters.size(); i++) {
            Map<YearMonth, BigDecimal> amounts = new HashMap<>();

            // Accumulate amounts for each filter, grouped by year and month
            for (AccountTransaction transaction : accountTransactions) {
                if (payeeFilters.get(i).getPayees().stream().map(d -> d.toLowerCase()).anyMatch(transaction.getHeader().toLowerCase()::contains)) {
                    added.add(transaction);
                    YearMonth ym = YearMonth.of(transaction.getDate().getYear(), transaction.getDate().getMonthValue());
                    // TODO: Why ROUND here? Try removing it!
                    amounts.merge(ym, transaction.getAmount().abs().setScale(0, BigDecimal.ROUND_DOWN), BigDecimal::add);
                }
            }

            // Set payee headers
            // Calculate payee invoices
            for (Map.Entry<YearMonth, BigDecimal> entry : amounts.entrySet()) {
                String alias = payeeFilters.get(i).getAlias();
                setCellValues(sheet.getCellByPosition(i + COLUMN_OFFSET, 0), alias, true, PASTEL_PEACH, HEADER_DEFAULT_SIZE, alias, StyleTypeDefinitions.HorizontalAlignmentType.DEFAULT);
                sheet.getCellByPosition(i + COLUMN_OFFSET, entry.getKey().getMonth().getValue()).setDoubleValue((double) Math.abs(entry.getValue().doubleValue()));
            }

            // Calculate average per payee
            String odfColName = getColumnName(i + COLUMN_OFFSET + 1);
            setCellValues(sheet.getCellByPosition(i + COLUMN_OFFSET, ROW_COUNT - 1), "", true, PASTEL_PINK);
            sheet.getCellByPosition(i + COLUMN_OFFSET, ROW_COUNT - 1).setFormula(String.format("=ROUND(AVERAGE(%s2:%s13);%s)", odfColName, odfColName, ROUNDING));
            // Calculate totals per payee
            setCellValues(sheet.getCellByPosition(i + COLUMN_OFFSET, ROW_COUNT), "", true, PASTEL_PURPLE);
            sheet.getCellByPosition(i + COLUMN_OFFSET, ROW_COUNT).setFormula(String.format("=ROUND(SUM(%s2:%s13);%s)", odfColName, odfColName, ROUNDING));
        }
        System.out.println("Removing stuff....");
        accountTransactions.removeAll(added);
        System.out.println(accountTransactions.stream().map(d -> d.toString()).collect(Collectors.toList()));
        System.out.println("Done!");
    }

    private void setHeaders(List<PayeeFilter> payeeFilters, Table sheet) {
        setCellValues(sheet.getCellByPosition(0, 0), MONTH, true, PASTEL_PEACH, HEADER_DEFAULT_SIZE, StyleTypeDefinitions.HorizontalAlignmentType.CENTER);
        setCellValues(sheet.getCellByPosition(payeeFilters.size() + COLUMN_OFFSET, 0), TOTAL, true, GREY, HEADER_DEFAULT_SIZE, StyleTypeDefinitions.HorizontalAlignmentType.RIGHT);
        setCellValues(sheet.getCellByPosition(0, ROW_COUNT - 1), AVERAGE, true, PASTEL_PINK, HEADER_DEFAULT_SIZE, StyleTypeDefinitions.HorizontalAlignmentType.RIGHT);
        setCellValues(sheet.getCellByPosition(0, ROW_COUNT), GRAND_TOTAL, true, PASTEL_PURPLE, HEADER_DEFAULT_SIZE, StyleTypeDefinitions.HorizontalAlignmentType.RIGHT);
        createMonthColumn(sheet);
    }

    private void setCellValues(Cell cell, String value, boolean bold) {
        setCellValues(cell, value, bold, null);
    }

    private void setCellValues(Cell cell, String value, boolean bold, Color color) {
        setCellValues(cell, value, bold, color, 10, StyleTypeDefinitions.HorizontalAlignmentType.DEFAULT);
    }

    private void setCellValues(Cell cell, String value, boolean bold, Color color, int fontSize, StyleTypeDefinitions.HorizontalAlignmentType alignmentType) {
        setCellValues(cell, value, bold, color, fontSize, null, alignmentType);
    }

    private void setCellValues(Cell cell, String value, boolean bold, Color color, int fontSize, String columnAlias, StyleTypeDefinitions.HorizontalAlignmentType alignmentType) {
        if (columnAlias != null) {
            List<String> parts = Arrays.asList(columnAlias.split("\\n"));
            cell.getTableColumn().setWidth(parts.stream().max(Comparator.comparingInt(String::length)).get().length() + COLUMN_WIDTH_MODIFIER);
        }
        cell.setHorizontalAlignment(alignmentType);
        cell.setStringValue(value);
        cell.setCellBackgroundColor(color);
        if (bold) {
            cell.setFont(new Font("", StyleTypeDefinitions.FontStyle.BOLD, fontSize));
        }
    }

    private void calcTotalsPerPayee(List<PayeeFilter> payeeFilters, Table sheet, int rowIndex, String function) {
        setCellValues(sheet.getCellByPosition(payeeFilters.size() + COLUMN_OFFSET, rowIndex), "", true, GREY);
        sheet.getCellByPosition(payeeFilters.size() + COLUMN_OFFSET, rowIndex)
                .setFormula(String.format(function + "%s2:%s13);%s)", getColumnName(payeeFilters.size() + COLUMN_OFFSET + 1), getColumnName(payeeFilters.size() + COLUMN_OFFSET + 1), ROUNDING));
    }

    private void calcMonthlyTotals(List<PayeeFilter> payeeFilters, Table sheet) {
        for (int i = 2; i < ROW_COUNT; i++) {
            setCellValues(sheet.getCellByPosition(payeeFilters.size() + COLUMN_OFFSET, i - 1), "", true);
            sheet.getCellByPosition(payeeFilters.size() + COLUMN_OFFSET, i - 1)
                    .setFormula(String.format("=IF(COUNTBLANK(B%s:%s)=%d;\"\";ROUND(SUM(B%s:%s);%d))", i, getColumnName(payeeFilters.size() + COLUMN_OFFSET) + i, payeeFilters.size(), i,
                            getColumnName(payeeFilters.size() + COLUMN_OFFSET) + i, ROUNDING));
        }
    }

    private void createMonthColumn(Table sheet) {
        setCellValues(sheet.getCellByPosition(0, 1), Month.JANUARY.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH, HEADER_DEFAULT_SIZE,
                StyleTypeDefinitions.HorizontalAlignmentType.RIGHT);
        setCellValues(sheet.getCellByPosition(0, 2), Month.FEBRUARY.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH, HEADER_DEFAULT_SIZE,
                StyleTypeDefinitions.HorizontalAlignmentType.RIGHT);
        setCellValues(sheet.getCellByPosition(0, 3), Month.MARCH.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH, HEADER_DEFAULT_SIZE,
                StyleTypeDefinitions.HorizontalAlignmentType.RIGHT);
        setCellValues(sheet.getCellByPosition(0, 4), Month.APRIL.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH, HEADER_DEFAULT_SIZE,
                StyleTypeDefinitions.HorizontalAlignmentType.RIGHT);
        setCellValues(sheet.getCellByPosition(0, 5), Month.MAY.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH, HEADER_DEFAULT_SIZE,
                StyleTypeDefinitions.HorizontalAlignmentType.RIGHT);
        setCellValues(sheet.getCellByPosition(0, 6), Month.JUNE.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH, HEADER_DEFAULT_SIZE,
                StyleTypeDefinitions.HorizontalAlignmentType.RIGHT);
        setCellValues(sheet.getCellByPosition(0, 7), Month.JULY.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH, HEADER_DEFAULT_SIZE,
                StyleTypeDefinitions.HorizontalAlignmentType.RIGHT);
        setCellValues(sheet.getCellByPosition(0, 8), Month.AUGUST.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH, HEADER_DEFAULT_SIZE,
                StyleTypeDefinitions.HorizontalAlignmentType.RIGHT);
        setCellValues(sheet.getCellByPosition(0, 9), Month.SEPTEMBER.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH, HEADER_DEFAULT_SIZE,
                StyleTypeDefinitions.HorizontalAlignmentType.RIGHT);
        setCellValues(sheet.getCellByPosition(0, 10), Month.OCTOBER.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH, HEADER_DEFAULT_SIZE,
                StyleTypeDefinitions.HorizontalAlignmentType.RIGHT);
        setCellValues(sheet.getCellByPosition(0, 11), Month.NOVEMBER.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH, HEADER_DEFAULT_SIZE,
                StyleTypeDefinitions.HorizontalAlignmentType.RIGHT);
        setCellValues(sheet.getCellByPosition(0, 12), Month.DECEMBER.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), true, PASTEL_PEACH, HEADER_DEFAULT_SIZE,
                StyleTypeDefinitions.HorizontalAlignmentType.RIGHT);
    }

    // TODO: Should be a static class in a Spreadsheet utility class!
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
