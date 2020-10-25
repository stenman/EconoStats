package se.perfektum.econostats.bank.nordea;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.perfektum.econostats.configuration.NordeaProperties;
import se.perfektum.econostats.domain.AccountTransaction;

public class NordeaCsvReaderTest {

    @InjectMocks
    NordeaCsvReader csvReader;

    @Mock
    NordeaProperties nordeaProperties;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void readCsvV1ShouldReturnAllLines() {
        when(nordeaProperties.getCsvFilePath()).thenReturn("src/test/resources/test1.csv");

        LocalDateTime now = LocalDateTime.now();
        List<AccountTransaction> actual = null;
        try {
            actual = csvReader.getAccountTransactionsFromFile(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Until I come up with a good way of testing this...
        for (AccountTransaction at : actual) {
            at.setStampInserted(now);
            at.setStampChanged(now);
        }

        //@formatter:off
        assertThat(actual, hasItems(
                new AccountTransaction.Builder()
                        .date(LocalDate.parse("2018-10-04", DateTimeFormatter.ISO_LOCAL_DATE))
                        .header("Reservation Kortköp ICA Matkasse")
                        .category("")
                        .amount(new BigDecimal("-439.00"))
                        .balance(null)
                        .stampInserted(now)
                        .stampChanged(now)
                        .currency("")
                        .name("")
                        .sender("")
                        .receiver("")
                        .build(),
                new AccountTransaction.Builder()
                        .date(LocalDate.parse("2018-10-03", DateTimeFormatter.ISO_LOCAL_DATE))
                        .header("Kortköp 181666 ICA MATKASSE")
                        .category("Household")
                        .amount(new BigDecimal("-439.00"))
                        .balance(new BigDecimal("4511.21"))
                        .stampInserted(now)
                        .stampChanged(now)
                        .currency("")
                        .name("")
                        .sender("")
                        .receiver("")
                        .build(),
                new AccountTransaction.Builder()
                        .date(LocalDate.parse("2018-10-03", DateTimeFormatter.ISO_LOCAL_DATE))
                        .header("John DOE,JOHN")
                        .category("")
                        .amount(new BigDecimal("-17.00"))
                        .balance(new BigDecimal("4950.21"))
                        .stampInserted(now)
                        .stampChanged(now)
                        .currency("")
                        .name("")
                        .sender("")
                        .receiver("")
                        .build(),
                new AccountTransaction.Builder()
                        .date(LocalDate.parse("2018-09-25", DateTimeFormatter.ISO_LOCAL_DATE))
                        .header("Lön")
                        .category("")
                        .amount(new BigDecimal("1000.00"))
                        .balance(new BigDecimal("3488.09"))
                        .stampInserted(now)
                        .stampChanged(now)
                        .currency("")
                        .name("")
                        .sender("")
                        .receiver("")
                        .build()));
        //@formatter:on
    }

    @Test
    public void readCsvV2ShouldReturnAllLines() {
        when(nordeaProperties.getCsvFilePath()).thenReturn("src/test/resources/test2.csv");
        LocalDateTime now = LocalDateTime.now();
        List<AccountTransaction> actual = null;
        try {
            actual = csvReader.getAccountTransactionsFromFile(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Until I come up with a good way of testing this...
        for (AccountTransaction at : actual) {
            at.setStampInserted(now);
            at.setStampChanged(now);
        }

        //@formatter:off
        assertThat(actual, hasItems(
                new AccountTransaction.Builder()
                        .date(LocalDate.parse("2020-10-22", DateTimeFormatter.ISO_LOCAL_DATE))
                        .header("Reservation Kortköp ICA City Ankeborg")
                        .category("")
                        .amount(new BigDecimal("-439.00"))
                        .balance(null)
                        .stampInserted(now)
                        .stampChanged(now)
                        .currency("SEK")
                        .name("")
                        .sender("810101-1234")
                        .receiver("")
                        .build(),
                new AccountTransaction.Builder()
                        .date(LocalDate.parse("2020-09-29", DateTimeFormatter.ISO_LOCAL_DATE))
                        .header("Betalning BG 1234-5678 Ankeborgs Elnät")
                        .category("")
                        .amount(new BigDecimal("-1200.00"))
                        .balance(new BigDecimal("10800.00"))
                        .stampInserted(now)
                        .stampChanged(now)
                        .currency("SEK")
                        .name("")
                        .sender("3051 02 12346")
                        .receiver("")
                        .build(),
                new AccountTransaction.Builder()
                        .date(LocalDate.parse("2020-08-31", DateTimeFormatter.ISO_LOCAL_DATE))
                        .header("Betalning BG 1234-5678 Ankeborgs Elnät")
                        .category("")
                        .amount(new BigDecimal("-1100.00"))
                        .balance(new BigDecimal("11900.00"))
                        .stampInserted(now)
                        .stampChanged(now)
                        .currency("SEK")
                        .name("")
                        .sender("3051 02 12346")
                        .receiver("")
                        .build(),
                new AccountTransaction.Builder()
                        .date(LocalDate.parse("2020-08-02", DateTimeFormatter.ISO_LOCAL_DATE))
                        .header("ÖVF DOE,JOHN")
                        .category("")
                        .amount(new BigDecimal("2000.00"))
                        .balance(new BigDecimal("9900.00"))
                        .stampInserted(now)
                        .stampChanged(now)
                        .currency("SEK")
                        .name("")
                        .sender("")
                        .receiver("810101-1234")
                        .build(),
                new AccountTransaction.Builder()
                        .date(LocalDate.parse("2020-07-28", DateTimeFormatter.ISO_LOCAL_DATE))
                        .header("Autogiro FISK.AKASSA")
                        .category("")
                        .amount(new BigDecimal("-100.66"))
                        .balance(new BigDecimal("10000.66"))
                        .stampInserted(now)
                        .stampChanged(now)
                        .currency("SEK")
                        .name("")
                        .sender("3051 02 12345")
                        .receiver("")
                        .build()
                ));
        //@formatter:on
    }
}
