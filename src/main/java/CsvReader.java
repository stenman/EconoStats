import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses a CSV file.
 */
public class CsvReader {

    //TODO: Should return accountTransaction objects...
    //TODO: An alternative is to simply transfer csv into the csv I want.... in that case this class is not needed!

    /**
     * Reads each line of provided csv file and strips the header if existing.
     *
     * @param csvFile    the file to parse
     * @param csvSplitBy the value which each value is separated by
     * @return list of lines without headers
     */
    public List<String> parseCsv(String csvFile, String csvSplitBy) {
        String line = "";
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] oneLine = line.split(csvSplitBy);
                lines.add(oneLine.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
