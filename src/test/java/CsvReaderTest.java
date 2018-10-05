import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CsvReaderTest {
    @Test
    public void readCsvShouldReturnAllLines() {
        CsvReader csvReader = new CsvReader();

        List<String[]> expected = new ArrayList<>();

        String[] expectedTransactions = {"asd"};

        expected.add(expectedTransactions);

        try {
            assertEquals(expected, csvReader.parseCsv("src/test/resources/test1.csv", ","));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
