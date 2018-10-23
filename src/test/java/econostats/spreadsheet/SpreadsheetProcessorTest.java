package econostats.spreadsheet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import se.perfektum.econostats.dao.AccountTransactionDao;
import se.perfektum.econostats.dao.IAccountTransactionDao;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.spreadsheet.SpreadsheetProcessor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpreadsheetProcessorTest {

    private IAccountTransactionDao accountTransactionDao = Mockito.mock(AccountTransactionDao.class);

    private SpreadsheetProcessor spreadsheetProcessor = new SpreadsheetProcessor(accountTransactionDao);

    @Before
    public void before() {
        List<AccountTransaction> accountTransactions = getAccountTransactions();
        Mockito.when(accountTransactionDao.loadAccountTransactions()).thenReturn(accountTransactions);
    }

    @Test
    public void createSpreadSheet() throws Exception {
        spreadsheetProcessor.createSpreadsheet(getPayeesConfig());
    }

    private List<String> getPayeesConfig() {
        List<String> payeesConfig = new ArrayList<>();
        payeesConfig.add("Inteleon AB");
        payeesConfig.add("ICA MATKASSE");
        payeesConfig.add("Spotify");
        return payeesConfig;
    }

    private List<AccountTransaction> getAccountTransactions() {
        LocalDateTime now = LocalDateTime.now();

        return Arrays.asList(new AccountTransaction.Builder()
                        .userId(1)
                        .accountId(1)
                        .date(LocalDate.parse("2018-10-04", DateTimeFormatter.ISO_LOCAL_DATE))
                        .name("Reservation Kortköp ICA Matkasse")
                        .category("")
                        .amount(-43900)
                        .balance(0)
                        .stampInserted(now)
                        .stampChanged(now)
                        .build(),
                new AccountTransaction.Builder()
                        .userId(1)
                        .accountId(1)
                        .date(LocalDate.parse("2018-10-03", DateTimeFormatter.ISO_LOCAL_DATE))
                        .name("Kortköp 181666 ICA MATKASSE")
                        .category("Household")
                        .amount(-43900)
                        .balance(451121)
                        .stampInserted(now)
                        .stampChanged(now)
                        .build(),
                new AccountTransaction.Builder()
                        .userId(1)
                        .accountId(1)
                        .date(LocalDate.parse("2018-10-03", DateTimeFormatter.ISO_LOCAL_DATE))
                        .name("John DOE,JOHN")
                        .category("")
                        .amount(-1700)
                        .balance(495021)
                        .stampInserted(now)
                        .stampChanged(now)
                        .build(),
                new AccountTransaction.Builder()
                        .userId(1)
                        .accountId(1)
                        .date(LocalDate.parse("2018-09-25", DateTimeFormatter.ISO_LOCAL_DATE))
                        .name("Lön")
                        .category("")
                        .amount(100000)
                        .balance(348809)
                        .stampInserted(now)
                        .stampChanged(now)
                        .build());
    }

}
