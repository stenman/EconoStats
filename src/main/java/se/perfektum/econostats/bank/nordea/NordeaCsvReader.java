package se.perfektum.econostats.bank.nordea;

import com.opencsv.CSVReaderHeaderAware;
import se.perfektum.econostats.bank.ICsvReader;
import se.perfektum.econostats.domain.AccountTransaction;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses a CSV file.
 */
public class NordeaCsvReader implements ICsvReader {
    /**
     * Reads each line of provided csv file and strips the header if existing.
     *
     * @param csvFile    the file to parse
     * @param csvSplitBy the value which each value is separated by
     * @return list of lines without headers
     */
    public List<AccountTransaction> parseCsv(String csvFile, String csvSplitBy, char[] charsToEscape) throws Exception {
        FileReader fr = null;
        try {
            fr = new FileReader(csvFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        List<AccountTransaction> accountTransactions = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        CSVReaderHeaderAware reader;
        try {
            reader = new CSVReaderHeaderAware(fr);
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                AccountTransaction at = new AccountTransaction();
                at.setDate(LocalDate.parse(nextLine[0], formatter));
                at.setName(nextLine[1]);
                at.setCategory(nextLine[2]);
                String amount = nextLine[3];
                String balance = nextLine[4];
                if (amount != null && !amount.equals("")) {
                    at.setAmount(Integer.parseInt(amount.replaceAll(",", "").replaceAll("\\.", "")));
                }
                if (balance != null && !balance.equals("")) {
                    at.setBalance(Integer.parseInt(balance.replaceAll(",", "").replaceAll("\\.", "")));
                }
                at.setStampInserted(LocalDateTime.now());
                at.setStampChanged(LocalDateTime.now());
                accountTransactions.add(at);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accountTransactions;
    }
}
