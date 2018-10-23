package econostats.spreadsheet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.odftoolkit.simple.SpreadsheetDocument;
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
        SpreadsheetDocument sd = spreadsheetProcessor.createSpreadsheet(getPayeesConfig());
    }

    private List<String> getPayeesConfig() {
        List<String> payeesConfig = new ArrayList<>();
        payeesConfig.add("Inteleon AB");
        payeesConfig.add("ICA MATKASSE");
        payeesConfig.add("Spotify");
        return payeesConfig;
    }

// STATISK
//    2018-09-28,Autogiro FRISKTANDV,,"-67,00","11.478,77"
//    2018-07-31,Autogiro FOLKSAM,,"-1000,00","13.703,77"
//    2018-08-31,Autogiro FOLKSAM,,"-1000,00","13.703,77"

// GRUPP + STATISK
//    2018-09-03,Nordea LIV 3051 66 66661,,"-25,00","5.239,77"
//    2018-09-03,Nordea LIV 3051 66 66662,,"-24,00","5.264,77"
//    2018-09-03,Nordea LIV 3051 66 66663,,"-78,00","5.288,77"
//    2018-09-03,Nordea LIV 3051 66 66664,,"-63,00","5.366,77"

// VARIERANDE
//    2018-07-11,Betalning BG 170-3453 Inteleon AB,,"-8,00","1.731,77"
//    2018-08-11,Betalning BG 170-3453 Inteleon AB,,"-58,00","1.731,77"
//    2018-09-11,Betalning BG 170-3453 Inteleon AB,,"-238,00","1.731,77"

// GRUPP + VARIERANDE
//    2018-08-29,Omsättning lån 3998 77 77771,,"-231,00","13.896,77"
//    2018-08-27,Omsättning lån 3998 77 77771,,"-1.008,00","0,77"
//    2018-08-27,Omsättning lån 3998 77 77772,,"-578,00","1.008,77"
//    2018-08-27,Omsättning lån 3991 77 77773,,"-2.395,00","1.586,77"

// SKALL EJ TAS MED (FINNS EJ I CONFIG)
//    2018-08-03,Överföring 112233-4455,,"-1.000,00","7.613,77"



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
