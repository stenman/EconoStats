package se.perfektum.econostats.dev;

import org.odftoolkit.simple.SpreadsheetDocument;

import java.awt.*;
import java.io.File;

public class TestUtilities {
    public static void openOds(SpreadsheetDocument doc, File path, String fileName) throws Exception {
        if (!path.exists()) {
            path.mkdir();
        }
        File file = new File(path, fileName);
        doc.save(file);
        Desktop desktop = Desktop.getDesktop();
        desktop.open(file);
    }
}
