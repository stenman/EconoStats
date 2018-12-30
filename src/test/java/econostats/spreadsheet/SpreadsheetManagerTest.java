package econostats.spreadsheet;

import org.apache.commons.io.IOUtils;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Test;
import org.mockito.Mockito;
import se.perfektum.econostats.common.JsonUtils;
import se.perfektum.econostats.dao.AccountTransactionDao;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.spreadsheet.OdfToolkitSpreadsheetManager;
import se.perfektum.econostats.spreadsheet.OdfToolkitSpreadsheetProcessor;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class SpreadsheetManagerTest {

    private AccountTransactionDao accountTransactionDao = Mockito.mock(AccountTransactionDao.class);
    private OdfToolkitSpreadsheetProcessor odfToolkitSpreadsheetProcessor = Mockito.mock(OdfToolkitSpreadsheetProcessor.class);
    private OdfToolkitSpreadsheetManager spreadsheetManager = new OdfToolkitSpreadsheetManager(accountTransactionDao, odfToolkitSpreadsheetProcessor);

    @Test
    public void mergeAccountTransactions() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        String transactions = IOUtils.toString(classLoader.getResourceAsStream("transactions-1.json"), "UTF-8");
        String localTransactions = IOUtils.toString(classLoader.getResourceAsStream("imported-transactions-1.json"), "UTF-8");
        String expectedTransactions = IOUtils.toString(classLoader.getResourceAsStream("transactions-1-expected.json"), "UTF-8");
        List<AccountTransaction> importedAccountTransactions = JsonUtils.getJsonElement(AccountTransaction.class, localTransactions);
        List<AccountTransaction> expected = JsonUtils.getJsonElement(AccountTransaction.class, expectedTransactions);

        List<AccountTransaction> actual = spreadsheetManager.mergeAccountTransactions(importedAccountTransactions, transactions);

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
