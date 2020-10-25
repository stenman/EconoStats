package se.perfektum.econostats.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

public class NordeaCsvV1ToV2Converter {

    public static void main(String[] args) {
        convert("C:\\EconoStats\\nordeaGemensamtNew.csv", "C:\\EconoStats\\nyfil.csv");
    }

    // This will take all input from inFile and put it in the selected order into
    // outFile.
    // Limitation: If there are quotations in the header (transaktion in this case),
    // they will be multiplied (ie. " --> """) for some reason. I couldn't quite get
    // it to work properly, so I'll leave it up to the user to fix in notepad or
    // something (simply replace """ with " in the file)
    private static void convert(String inFile, String outFile) {
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.detectFormatAutomatically();
        parserSettings.setKeepQuotes(true);

        CsvParser parser = new CsvParser(parserSettings);

        List<String[]> allLines = parser.parseAll(new File(inFile));

        ByteArrayOutputStream csvResult = new ByteArrayOutputStream();

        Writer outputWriter = new OutputStreamWriter(csvResult);

        CsvWriterSettings writerSettings = new CsvWriterSettings();
        writerSettings.setNullValue("");
        writerSettings.setEmptyValue("");
        writerSettings.setSkipEmptyLines(false);
        writerSettings.setHeaders("Datum", "Belopp", "Transaktion", "Saldo");
        writerSettings.selectFields("Datum", "Transaktion", "Kategori", "Belopp", "Saldo");
        CsvWriter writer = new CsvWriter(outputWriter, writerSettings);

        writer.writeHeaders();

        for (int i = 1; i < allLines.size(); i++) {
            String amount = allLines.get(i)[3];
            String balance = null;

            if (amount != null) {
                amount = amount.trim().replaceAll("\"", "").replaceAll("\\.", "").replaceAll(",", ".").replaceAll("\u2212", "\u002D");
            }
            if (allLines.get(i).length > 4 && allLines.get(i)[4] != null) {
                balance = allLines.get(i)[4].trim().replaceAll("\"", "").replaceAll("\\.", "").replaceAll(",", ".").replaceAll("\u2212", "\u002D");
            }
            String[] result = { allLines.get(i)[0], allLines.get(i)[1], allLines.get(i)[2], amount, balance };
            writer.writeRow(result);
        }

        writer.close();

        writeToFile(csvResult, outFile);
//        System.out.println(csvResult.toString());
        System.out.println("Done!");
    }

    private static void writeToFile(ByteArrayOutputStream csvResult, String outFile) {
        try (OutputStream outputStream = new FileOutputStream(outFile)) {
            csvResult.writeTo(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
