package se.perfektum.econostats.bank.nordea;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import se.perfektum.econostats.bank.CsvReader;
import se.perfektum.econostats.configuration.NordeaProperties;
import se.perfektum.econostats.domain.AccountTransaction;

/**
 * Parses a CSV file.
 */
@Component
public class NordeaCsvReader implements CsvReader {

    final Logger LOGGER = LoggerFactory.getLogger(NordeaCsvReader.class);

    private final static String FIRST_COLUMN_NORDEA_VERSION_1 = "Datum";
    private final static String FIRST_COLUMN_NORDEA_VERSION_2 = "Bokföringsdag";

    @Autowired
    private NordeaProperties nordeaProperties;

    /**
     * Reads a csv file from disk and parses it to AccountTransactions. If no path
     * is provided, the configuration file path will be used.
     *
     * @param csvFile Path to csv file to parse
     * @return List of lines without headers
     * @throws NumberFormatException
     */
    public List<AccountTransaction> getAccountTransactionsFromFile(String csvFile) throws NumberFormatException {
        csvFile = csvFile == null || csvFile.isEmpty() ? nordeaProperties.getCsvFilePath() : csvFile;
        LOGGER.debug(String.format("Parsing file '%s'", csvFile));
        return parseCsv(csvFile);
    }

    /**
     * Reads each line of provided csv file and strips the header if existing.
     */
    private List<AccountTransaction> parseCsv(String csvFile) {
        CsvParserSettings settings = new CsvParserSettings();
        settings.detectFormatAutomatically();

        CsvParser parser = new CsvParser(settings);

        List<String[]> allLines = parser.parseAll(new File(csvFile));

        CsvVersion version = prepareLinesAndGetVersion(allLines);

        List<AccountTransaction> ats = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (version == CsvVersion.V1) {
            for (String[] line : allLines) {

                AccountTransaction at = new AccountTransaction();

                at.setDate(LocalDate.parse(line[0], formatter));
                at.setHeader(Objects.toString(line[1], ""));
                at.setCategory(Objects.toString(line[2], ""));
                String amount = line[3];
                String balance = null;
                // Balance may not always exist
                if (line.length > 4 && line[4] != null) {
                    balance = line[4];
                }
                if (amount != null && !amount.equals("")) {
                    try {
                        // Hyphens
                        // For some reason Nordea sometimes throws in (or lets through)
                        // an arithmetic operator minus sign instead of a regular hyphen-minus.
                        // Separators
                        // For some reason Nordea uses different notions of presenting the amount
                        // Sometimes there might be a comma("66,00"), sometimes punctuation ("66.00"),
                        // sometimes nothing ("66")
                        at.setAmount(new BigDecimal(amount.trim().replaceAll("\\.", "").replaceAll(",", ".").replaceAll("\u2212", "\u002D")));
                    } catch (NumberFormatException nfe) {
                        throw new NumberFormatException(String.format("Failed to parse the following: '%s'\n %s", amount, nfe));
                    }
                }
                if (balance != null && !balance.equals("")) {
                    balance = balance.trim();
                    at.setBalance(new BigDecimal(balance.trim().replaceAll("\\.", "").replaceAll(",", ".")));
                }
                at.setStampInserted(LocalDateTime.now());
                at.setStampChanged(LocalDateTime.now());
                ats.add(at);
            }
        }

        else if (version == CsvVersion.V2) {
            for (String[] line : allLines) {

                AccountTransaction at = new AccountTransaction();
                at.setDate(LocalDate.parse(line[0], formatter));
                String amount = line[1];
                at.setSender(Objects.toString(line[2], ""));
                at.setReceiver(Objects.toString(line[3], ""));
                at.setName(Objects.toString(line[4], ""));
                at.setHeader(Objects.toString(line[5], ""));
                String balance = line[6];
                at.setCurrency(Objects.toString(line[7], ""));
                at.setStampInserted(LocalDateTime.now());
                at.setStampChanged(LocalDateTime.now());

                if (amount != null && !amount.equals("")) {
                    try {
                        at.setAmount(new BigDecimal(amount.replaceAll(",", ".")));
                    } catch (NumberFormatException nfe) {
                        throw new NumberFormatException(String.format("Failed to parse the following: '%s'\\n %s", amount, nfe));
                    }
                }
                if (balance != null && !balance.equals("")) {
                    try {
                        at.setBalance(new BigDecimal(balance.replaceAll(",", ".")));
                    } catch (NumberFormatException nfe) {
                        throw new NumberFormatException(String.format("Failed to parse the following: '%s'\\n %s", amount, nfe));
                    }
                }
                ats.add(at);
            }
        } else {
            // File error! Exit!
            // TODO: Throw a custom exception here, that bubbles up to EconostatsController
            // and shows a message to the user of the exact problem!
            return Collections.emptyList();
        }
        return ats;
    }

    private CsvVersion prepareLinesAndGetVersion(List<String[]> allLines) {
        LOGGER.debug(String.format("Preparing lines and determining version of csv file"));

        String firstLineFirstColumn = allLines.get(0)[0];
        int firstLineLength = allLines.get(0).length;

        CsvVersion version = CsvVersion.UNKNOWN;

        if (firstLineFirstColumn.equalsIgnoreCase(FIRST_COLUMN_NORDEA_VERSION_1)) {
            LOGGER.debug(String.format("Headers found. Removing headers from input."));
            allLines.remove(0);
            version = CsvVersion.V1;
        } else if (firstLineFirstColumn.equalsIgnoreCase(FIRST_COLUMN_NORDEA_VERSION_2)) {
            LOGGER.debug(String.format("Headers found. Removing headers from input."));
            allLines.remove(0);
            version = CsvVersion.V2;
        } else if (firstLineLength == 5) {
            LOGGER.debug(String.format("Simple V1"));
            version = CsvVersion.V1;
        } else if (firstLineLength == 8) {
            LOGGER.debug(String.format("Simple V2"));
            version = CsvVersion.V2;
        }
        LOGGER.debug(String.format("Csv file seems to be version: %s", version));
        return version;
    }
}
