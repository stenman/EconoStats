package econostats.spreadsheet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import se.perfektum.econostats.dao.AccountTransactionDao;
import se.perfektum.econostats.dao.IAccountTransactionDao;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;
import se.perfektum.econostats.spreadsheet.SpreadsheetProcessor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SpreadsheetProcessorTest {

    private IAccountTransactionDao accountTransactionDao = Mockito.mock(AccountTransactionDao.class);

    private SpreadsheetProcessor spreadsheetProcessor = new SpreadsheetProcessor(accountTransactionDao);

    private static final int ROW_COUNT = 15;
    private static final int COLUMN_OFFSET = 1;

    @Before
    public void before() {
        List<AccountTransaction> accountTransactions = getAccountTransactions();
        Mockito.when(accountTransactionDao.loadAccountTransactions()).thenReturn(accountTransactions);
    }

    @Test
    public void simpleHappyFlow() throws Exception {
        List<PayeeFilter> payeeFilters = new ArrayList<>();
        PayeeFilter pc = new PayeeFilter();
        pc.setUserId(1);
        pc.setAccountId(1);
        pc.setPayee("Autogiro FRISKTANDV");
        pc.setAlias("Frisktandvården");
        pc.setGroup(Character.MIN_VALUE);
        pc.setVarying(false);
        payeeFilters.add(pc);

        final int columnsToAssert = payeeFilters.size() + 1; // number of configs + tot/avg column

        Mockito.when(accountTransactionDao.loadPayeeFilter()).thenReturn(payeeFilters);

        SpreadsheetDocument sd = spreadsheetProcessor.createSpreadsheet(payeeFilters);

        Table sheet = sd.getSheetByIndex(0);

        assertMonths(sheet);

        String[][] c = {{"Frisktandvården"
                , ""
                , ""
                , ""
                , ""
                , ""
                , ""
                , ""
                , "67.0"
                , ""
                , ""
                , ""
                , ""
                , "=AVERAGE(B2:B13)"
                , "=SUM(B2:B13)"}
                , {"Total"
                , "=SUM(B2:B2)"
                , "=SUM(B3:B3)"
                , "=SUM(B4:B4)"
                , "=SUM(B5:B5)"
                , "=SUM(B6:B6)"
                , "=SUM(B7:B7)"
                , "=SUM(B8:B8)"
                , "=SUM(B9:B9)"
                , "=SUM(B10:B10)"
                , "=SUM(B11:B11)"
                , "=SUM(B12:B12)"
                , "=SUM(B13:B13)"
                , "=AVERAGE(C2:C13)"
                , "=SUM(C2:C13)"}};
        for (int i = 0; i < columnsToAssert; i++) {
            for (int j = 0; j < ROW_COUNT; j++) {
                Cell cell = sheet.getCellByPosition(i + COLUMN_OFFSET, j);
                if (cell != null && cell.getFormula() != null && !cell.getFormula().isEmpty()) {
                    assertEquals(c[i][j], cell.getFormula());
                } else {
                    assertEquals(c[i][j], cell.getDisplayText());
                }
            }
        }

        assertEquals(3, sheet.getColumnCount());
        assertEquals(15, sheet.getRowCount());
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
