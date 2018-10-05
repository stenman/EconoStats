import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses a CSV file.
 */
public class CsvReader {
    /**
     * Reads each line of provided csv file and strips the header if existing.
     *
     * @param csvFile    the file to parse
     * @param csvSplitBy the value which each value is separated by
     * @return list of lines without headers
     */
    public List<String[]> parseCsv(String csvFile, String csvSplitBy) throws Exception {
        String line;
        List<String[]> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String[] headers = br.readLine().split(csvSplitBy);
            if (headers != null) {
                lines.add(headers);
                while ((line = br.readLine()) != null) {
                    String[] data;
                    data = line.split(csvSplitBy);
                    if (data.length != headers.length) {
                        throw new Exception("Invalid Data - headers count " + headers.length + " does not match with data count " + data.length);
                    }
                    lines.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
