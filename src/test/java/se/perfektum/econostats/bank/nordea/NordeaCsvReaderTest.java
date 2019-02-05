package se.perfektum.econostats.bank.nordea;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import se.perfektum.econostats.configuration.NordeaProperties;
import se.perfektum.econostats.domain.AccountTransaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class NordeaCsvReaderTest {

    @InjectMocks
    NordeaCsvReader csvReader;

    @Mock
    NordeaProperties nordeaProperties;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        when(nordeaProperties.getCsvFilePath()).thenReturn("src/test/resources/test1.csv");
    }

    @Test
    public void readCsvShouldReturnAllLines() {
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

        assertThat(actual, hasItems(
                new AccountTransaction.Builder()
                        .date(LocalDate.parse("2018-10-04", DateTimeFormatter.ISO_LOCAL_DATE))
                        .name("Reservation Kortköp ICA Matkasse")
                        .category("")
                        .amount(-43900)
                        .balance(0)
                        .stampInserted(now)
                        .stampChanged(now)
                        .build(),
                new AccountTransaction.Builder()
                        .date(LocalDate.parse("2018-10-03", DateTimeFormatter.ISO_LOCAL_DATE))
                        .name("Kortköp 181666 ICA MATKASSE")
                        .category("Household")
                        .amount(-43900)
                        .balance(451121)
                        .stampInserted(now)
                        .stampChanged(now)
                        .build(),
                new AccountTransaction.Builder()
                        .date(LocalDate.parse("2018-10-03", DateTimeFormatter.ISO_LOCAL_DATE))
                        .name("John DOE,JOHN")
                        .category("")
                        .amount(-1700)
                        .balance(495021)
                        .stampInserted(now)
                        .stampChanged(now)
                        .build(),
                new AccountTransaction.Builder()
                        .date(LocalDate.parse("2018-09-25", DateTimeFormatter.ISO_LOCAL_DATE))
                        .name("Lön")
                        .category("")
                        .amount(100000)
                        .balance(348809)
                        .stampInserted(now)
                        .stampChanged(now)
                        .build()));
    }
}
