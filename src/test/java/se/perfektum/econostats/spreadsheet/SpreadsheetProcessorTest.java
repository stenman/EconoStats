package se.perfektum.econostats.spreadsheet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import se.perfektum.econostats.common.JsonUtils;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SpreadsheetProcessorTest {
    private OdfToolkitSpreadsheetProcessor spreadsheetProcessor = new OdfToolkitSpreadsheetProcessor();

    private static final int ROW_COUNT = 15;
    private static final int COLUMN_OFFSET = 1;

    @Test
    public void singlePayee_oneColumn() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        String payees = IOUtils.toString(classLoader.getResourceAsStream("payeeFilters-1.json"), "UTF-8");
        String transactions = IOUtils.toString(classLoader.getResourceAsStream("transactions-1.json"), "UTF-8");
        List<PayeeFilter> payeeFilters = JsonUtils.getJsonElement(PayeeFilter.class, payees);
        List<AccountTransaction> accountTransactions = JsonUtils.getJsonElement(AccountTransaction.class, transactions);

        Gson gson = new GsonBuilder().create();
        String[][] sheetTestData2017 = gson.fromJson(getSheetTestData("sheetTestData-2017.json"), String[][].class);
        String[][] sheetTestData2018 = gson.fromJson(getSheetTestData("sheetTestData-2018.json"), String[][].class);

        SpreadsheetDocument sd = spreadsheetProcessor.createSpreadsheet(accountTransactions, payeeFilters);

        // TEMP DEV
//        TestUtilities.openOds(sd, new File("c:/temp/testdata/"), "simpleodf.ods");
        // TEMP DEV

        Table sheet2017 = sd.getSheetByIndex(0);
        Table sheet2018 = sd.getSheetByIndex(1);

        assertMonths(sheet2017);
        assertSheetData(sheetTestData2017, sheet2017);
        assertEquals(5, sheet2017.getColumnCount());
        assertEquals(15, sheet2017.getRowCount());
        assertMonths(sheet2018);
        assertSheetData(sheetTestData2018, sheet2018);
        assertEquals(5, sheet2018.getColumnCount());
        assertEquals(15, sheet2018.getRowCount());
    }

    private String getSheetTestData(String name) {
        String result = "";
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            result = IOUtils.toString(classLoader.getResourceAsStream(name), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void assertSheetData(String[][] expectedSheetData, Table sheet) {
        for (int i = 0; i < expectedSheetData.length; i++) {
            for (int j = 0; j < ROW_COUNT; j++) {
                Cell cell = sheet.getCellByPosition(i + COLUMN_OFFSET, j);
                if (cell != null && cell.getFormula() != null && !cell.getFormula().isEmpty()) {
                    assertEquals(expectedSheetData[i][j], cell.getFormula());
                } else {
                    assertEquals(expectedSheetData[i][j], cell.getDisplayText());
                }
            }
        }
    }

    private void assertMonths(Table sheet) {
        assertEquals("Month", sheet.getCellByPosition(0, 0).getStringValue());
        assertEquals("Jan", sheet.getCellByPosition(0, 1).getStringValue());
        assertEquals("Feb", sheet.getCellByPosition(0, 2).getStringValue());
        assertEquals("Mar", sheet.getCellByPosition(0, 3).getStringValue());
        assertEquals("Apr", sheet.getCellByPosition(0, 4).getStringValue());
        assertEquals("May", sheet.getCellByPosition(0, 5).getStringValue());
        assertEquals("Jun", sheet.getCellByPosition(0, 6).getStringValue());
        assertEquals("Jul", sheet.getCellByPosition(0, 7).getStringValue());
        assertEquals("Aug", sheet.getCellByPosition(0, 8).getStringValue());
        assertEquals("Sep", sheet.getCellByPosition(0, 9).getStringValue());
        assertEquals("Oct", sheet.getCellByPosition(0, 10).getStringValue());
        assertEquals("Nov", sheet.getCellByPosition(0, 11).getStringValue());
        assertEquals("Dec", sheet.getCellByPosition(0, 12).getStringValue());
    }
}
