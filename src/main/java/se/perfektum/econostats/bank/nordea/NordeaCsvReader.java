package se.perfektum.econostats.bank.nordea;

import com.opencsv.CSVReaderHeaderAware;
import se.perfektum.econostats.bank.CsvReader;
import se.perfektum.econostats.domain.AccountTransaction;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses a CSV file.
 */
public class NordeaCsvReader implements CsvReader {
    /**
     * Reads each line of provided csv file and strips the header if existing.
     *
     * @param csvFile    the file to parse
     * @param csvSplitBy the value which each value is separated by
     * @return list of lines without headers
     */
    public List<AccountTransaction> parseCsv(String csvFile, String csvSplitBy, char[] charsToEscape) throws NumberFormatException {

        BufferedReader br = null;
        try {
            File file = new File(csvFile);
            br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), "UTF8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        List<AccountTransaction> accountTransactions = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        CSVReaderHeaderAware reader;
        try {
            reader = new CSVReaderHeaderAware(br);
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (!nextLine[0].isEmpty()) {
                    AccountTransaction at = new AccountTransaction();
                    at.setDate(LocalDate.parse(nextLine[0], formatter));
                    at.setName(nextLine[1]);
                    at.setCategory(nextLine[2]);
                    String amount = nextLine[3];
                    String balance = nextLine[4];
                    if (amount != null && !amount.equals("")) {
                        try {
                            // For some reason Nordea sometimes throws in (or lets through)
                            // an arithmetic operator minus sign instead of a regular hyphen-minus.
                            amount = amount.replace("\u2212", "\u002D");
                            // For some reason Nordea uses different notions of presenting the amount
                            // This is a cheap fix that might not be 100% water proof
                            if (amount.contains(",")) {
                                at.setAmount(Integer.parseInt(amount.replaceAll(",", "").replaceAll("\\.", "").trim()));
                            } else {
                                at.setAmount(Integer.parseInt(amount + "00"));
                            }
                        } catch (NumberFormatException nfe) {
                            throw new NumberFormatException(String.format("Failed to parse the following: '%s'\\n %s", amount, nfe));
                        }
                    }
                    if (balance != null && !balance.equals("")) {
                        at.setBalance(Integer.parseInt(balance.replaceAll(",", "").replaceAll("\\.", "")));
                    }
                    at.setStampInserted(LocalDateTime.now());
                    at.setStampChanged(LocalDateTime.now());
                    accountTransactions.add(at);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accountTransactions;
    }
}
