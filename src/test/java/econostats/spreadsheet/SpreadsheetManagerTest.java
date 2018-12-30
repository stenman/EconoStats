package econostats.spreadsheet;

import org.apache.commons.io.IOUtils;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Test;
import org.mockito.Mockito;
import se.perfektum.econostats.common.JsonUtils;
import se.perfektum.econostats.dao.AccountTransactionDao;
import se.perfektum.econostats.domain.PayeeFilter;
import se.perfektum.econostats.spreadsheet.OdfToolkitSpreadsheetManager;
import se.perfektum.econostats.spreadsheet.OdfToolkitSpreadsheetProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class SpreadsheetManagerTest {

    private AccountTransactionDao accountTransactionDao = Mockito.mock(AccountTransactionDao.class);
    private OdfToolkitSpreadsheetProcessor odfToolkitSpreadsheetProcessor = Mockito.mock(OdfToolkitSpreadsheetProcessor.class);
    private OdfToolkitSpreadsheetManager spreadsheetManager = new OdfToolkitSpreadsheetManager(accountTransactionDao, odfToolkitSpreadsheetProcessor);

//    @Test
//    public void syncPayeeFilters() throws IOException {
//        ClassLoader classLoader = getClass().getClassLoader();
//        String transactions = IOUtils.toString(classLoader.getResourceAsStream("transactions-1.json"), "UTF-8");
//        String payeeFilters = IOUtils.toString(classLoader.getResourceAsStream("local-payeeFilters-2.json"), "UTF-8");
//        List<PayeeFilter> localPayeeFilters = JsonUtils.getJsonElement(PayeeFilter.class, payeeFilters);
//
//        List<PayeeFilter> actual = new ArrayList<>();
//        PayeeFilter pf1 = new PayeeFilter();
//        PayeeFilter pf2 = new PayeeFilter();
//        pf1.setPayeeName("Autogiro CSN");
//        pf1.setAlias("CSN");
//        pf1.setGroup(Character.MIN_VALUE);
//        pf1.setVarying(false);
//        pf2.setPayeeName("Betalning BG 170-3453 Inteleon AB");
//        pf2.setAlias("Parkering");
//        pf2.setGroup(Character.MIN_VALUE);
//        pf2.setVarying(false);
//        actual.addAll(Arrays.asList(pf1, pf2));
//
//        assertEquals(actual, spreadsheetManager.syncPayeeFilters(localPayeeFilters, transactions));
////        assertThat(actual, IsIterableContainingInAnyOrder.containsInAnyOrder(spreadsheetManager.syncPayeeFilters(localPayeeFilters, transactions)));
//
//    }

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
