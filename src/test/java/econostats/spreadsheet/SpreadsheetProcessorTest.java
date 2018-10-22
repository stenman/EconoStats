package econostats.spreadsheet;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SpreadsheetProcessorTest {

    @Test
    public void createSpreadSheet() {

    }

    private List<String> getPayeesConfig() {
        List<String> payeesConfig = new ArrayList<>();
        payeesConfig.add("Inteleon AB");
        payeesConfig.add("ICA MATKASSE");
        payeesConfig.add("Spotify");
        return payeesConfig;
    }

}
