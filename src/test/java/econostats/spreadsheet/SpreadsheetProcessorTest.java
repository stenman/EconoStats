package econostats.spreadsheet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import se.perfektum.econostats.dao.AccountTransactionDao;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;
import se.perfektum.econostats.spreadsheet.OdfToolkitSpreadsheetProcessor;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SpreadsheetProcessorTest {

    private AccountTransactionDao accountTransactionDao = Mockito.mock(AccountTransactionDao.class);

    private OdfToolkitSpreadsheetProcessor spreadsheetProcessor = new OdfToolkitSpreadsheetProcessor(accountTransactionDao);

    private static final int ROW_COUNT = 15;
    private static final int COLUMN_OFFSET = 1;

    @Before
    public void before() throws IOException, GeneralSecurityException {
        List<AccountTransaction> accountTransactions = getAccountTransactions();
        Mockito.when(accountTransactionDao.getAccountTransactions()).thenReturn(accountTransactions);
    }

    @Test
    public void singlePayee_oneColumn() throws Exception {
        List<PayeeFilter> payeeFilters = new ArrayList<>();
        PayeeFilter pc = new PayeeFilter();
        pc.setPayeeName("Autogiro FRISKTANDV");
        pc.setAlias("Frisktandvården");
        pc.setGroup(Character.MIN_VALUE);
        pc.setVarying(false);
        payeeFilters.add(pc);

        Gson gson = new GsonBuilder().create();
        String[][] sheetTestData = gson.fromJson(getSheetTestData(), String[][].class);

        Mockito.when(accountTransactionDao.getPayeeFilter()).thenReturn(payeeFilters);

        SpreadsheetDocument sd = spreadsheetProcessor.createSpreadsheet(payeeFilters);
        Table sheet = sd.getSheetByIndex(0);

        assertMonths(sheet);
        assertSheetData(sheet, sheetTestData);
        assertEquals(3, sheet.getColumnCount());
        assertEquals(15, sheet.getRowCount());
    }

    private String getSheetTestData() {
        String result = "";
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            result = IOUtils.toString(classLoader.getResourceAsStream("sheetTestData.json"), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void assertSheetData(Table sheet, String[][] expectedSheetData) {
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

    private List<AccountTransaction> getAccountTransactions() {
        LocalDateTime now = LocalDateTime.now();

        return Arrays.asList(new AccountTransaction.Builder()
                        .userId(1)
                        .accountId(1)
                        .date(LocalDate.parse("2018-08-28", DateTimeFormatter.ISO_LOCAL_DATE))
                        .name("Autogiro FRISKTANDV")
                        .category("")
                        .amount(-6700)
                        .balance(0)
                        .stampInserted(now)
                        .stampChanged(now)
                        .build(),
                new AccountTransaction.Builder()
                        .userId(1)
                        .accountId(1)
                        .date(LocalDate.parse("2018-08-31", DateTimeFormatter.ISO_LOCAL_DATE))
                        .name("Autogiro FOLKSAM")
                        .category("Household")
                        .amount(-100000)
                        .balance(0)
                        .stampInserted(now)
                        .stampChanged(now)
                        .build(),
                new AccountTransaction.Builder()
                        .userId(1)
                        .accountId(1)
                        .date(LocalDate.parse("2018-09-30", DateTimeFormatter.ISO_LOCAL_DATE))
                        .name("Autogiro FOLKSAM")
                        .category("")
                        .amount(-100000)
                        .balance(0)
                        .stampInserted(now)
                        .stampChanged(now)
                        .build(),
                new AccountTransaction.Builder()
                        .userId(1)
                        .accountId(1)
                        .date(LocalDate.parse("2018-07-11", DateTimeFormatter.ISO_LOCAL_DATE))
                        .name("Betalning BG 170-3453 Inteleon AB")
                        .category("")
                        .amount(-800)
                        .balance(0)
                        .stampInserted(now)
                        .stampChanged(now)
                        .build(),
                new AccountTransaction.Builder()
                        .userId(1)
                        .accountId(1)
                        .date(LocalDate.parse("2018-08-11", DateTimeFormatter.ISO_LOCAL_DATE))
                        .name("Betalning BG 170-3453 Inteleon AB")
                        .category("")
                        .amount(-5800)
                        .balance(0)
                        .stampInserted(now)
                        .stampChanged(now)
                        .build(),
                new AccountTransaction.Builder()
                        .userId(1)
                        .accountId(1)
                        .date(LocalDate.parse("2018-09-11", DateTimeFormatter.ISO_LOCAL_DATE))
                        .name("Betalning BG 170-3453 Inteleon AB")
                        .category("")
                        .amount(-23800)
                        .balance(0)
                        .stampInserted(now)
                        .stampChanged(now)
                        .build(),
                new AccountTransaction.Builder()
                        .userId(1)
                        .accountId(1)
                        .date(LocalDate.parse("2018-09-29", DateTimeFormatter.ISO_LOCAL_DATE))
                        .name("Omsättning lån 3998 77 77771")
                        .category("")
                        .amount(-23100)
                        .balance(0)
                        .stampInserted(now)
                        .stampChanged(now)
                        .build(),
                new AccountTransaction.Builder()
                        .userId(1)
                        .accountId(1)
                        .date(LocalDate.parse("2018-09-27", DateTimeFormatter.ISO_LOCAL_DATE))
                        .name("Omsättning lån 3998 77 77771")
                        .category("")
                        .amount(-100800)
                        .balance(0)
                        .stampInserted(now)
                        .stampChanged(now)
                        .build(),
                new AccountTransaction.Builder()
                        .userId(1)
                        .accountId(1)
                        .date(LocalDate.parse("2018-09-27", DateTimeFormatter.ISO_LOCAL_DATE))
                        .name("Omsättning lån 3998 77 77772")
                        .category("")
                        .amount(-57800)
                        .balance(0)
                        .stampInserted(now)
                        .stampChanged(now)
                        .build(),
                new AccountTransaction.Builder()
                        .userId(1)
                        .accountId(1)
                        .date(LocalDate.parse("2018-09-27", DateTimeFormatter.ISO_LOCAL_DATE))
                        .name("Omsättning lån 3998 77 77773")
                        .category("")
                        .amount(-239500)
                        .balance(0)
                        .stampInserted(now)
                        .stampChanged(now)
                        .build());
    }
}
