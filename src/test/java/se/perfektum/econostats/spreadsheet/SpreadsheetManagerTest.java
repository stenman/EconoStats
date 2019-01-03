package se.perfektum.econostats.spreadsheet;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.apache.commons.io.IOUtils;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.slf4j.LoggerFactory;
import se.perfektum.econostats.common.JsonUtils;
import se.perfektum.econostats.domain.AccountTransaction;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class SpreadsheetManagerTest {

    private Appender mockAppender;
    private OdfToolkitSpreadsheetProcessor odfToolkitSpreadsheetProcessor = mock(OdfToolkitSpreadsheetProcessor.class);
    private OdfToolkitSpreadsheetManager spreadsheetManager = new OdfToolkitSpreadsheetManager(odfToolkitSpreadsheetProcessor);

    @Before
    public void before() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        mockAppender = mock(Appender.class);
        when(mockAppender.getName()).thenReturn("MOCK");
        root.addAppender(mockAppender);
    }

    @Test
    public void mergeAccountTransactions_regular() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        String transactions = IOUtils.toString(classLoader.getResourceAsStream("transactions-1.json"), "UTF-8");
        String localTransactions = IOUtils.toString(classLoader.getResourceAsStream("imported-transactions-1.json"), "UTF-8");
        String expectedTransactions = IOUtils.toString(classLoader.getResourceAsStream("transactions-1-expected.json"), "UTF-8");
        List<AccountTransaction> importedAccountTransactions = JsonUtils.getJsonElement(AccountTransaction.class, localTransactions);
        List<AccountTransaction> expected = JsonUtils.getJsonElement(AccountTransaction.class, expectedTransactions);

        List<AccountTransaction> actual = spreadsheetManager.mergeAccountTransactions(importedAccountTransactions, transactions);

        assertThat(expected, IsIterableContainingInAnyOrder.containsInAnyOrder(actual.toArray()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mergeAccountTransactions_similar_transactions() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        String transactions = IOUtils.toString(classLoader.getResourceAsStream("transactions-1.json"), "UTF-8");
        String localTransactions = IOUtils.toString(classLoader.getResourceAsStream("imported-transactions-2.json"), "UTF-8");
        String expectedTransactions = IOUtils.toString(classLoader.getResourceAsStream("transactions-2-expected.json"), "UTF-8");
        List<AccountTransaction> importedAccountTransactions = JsonUtils.getJsonElement(AccountTransaction.class, localTransactions);
        List<AccountTransaction> expected = JsonUtils.getJsonElement(AccountTransaction.class, expectedTransactions);

        List<AccountTransaction> actual = spreadsheetManager.mergeAccountTransactions(importedAccountTransactions, transactions);

        verify(mockAppender).doAppend(argThat(new ArgumentMatcher() {
            @Override
            public boolean matches(final Object argument) {
                return ((LoggingEvent) argument).getFormattedMessage().contains("Imported transactions contains one or more duplicate transactions. This will result in loss of as least one transaction (by distinction)!This may occur if there are eg. two deposits and one withdrawal with the exact same amount on the same day.BE ADVISED that this might yield erroneous results! Please check your imported file!");
            }
        }));

        assertThat(expected, IsIterableContainingInAnyOrder.containsInAnyOrder(actual.toArray()));
    }

//    @Test
//    public void createNewSpreadsheet() throws Exception {
//        ClassLoader classLoader = getClass().getClassLoader();
//        String file = IOUtils.toString(classLoader.getResourceAsStream("payeeFilters.json"), "UTF-8");
//        List<AccountTransaction> accountTransactions = JsonUtils.getJsonElement(AccountTransaction.class, file);
//        List<PayeeFilter> payeeFilters = JsonUtils.getJsonElement(PayeeFilter.class, file);
//
//        Gson gson = new GsonBuilder().create();
//        String[][] sheetTestData = gson.fromJson(getSheetTestData(), String[][].class);
//
//        SpreadsheetDocument sd = spreadsheetProcessor.createSpreadsheet(accountTransactions, payeeFilters);
//    }
}
