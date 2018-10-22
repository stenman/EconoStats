package se.perfektum.econostats.research;

import org.jopendocument.dom.OOUtils;
import org.jopendocument.dom.spreadsheet.MutableCell;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ExperimentJOpenDocument {

    public static void main(String[] args) throws IOException {

        // Create the data to save.
        final Object[][] data = new Object[6][2];
        data[0] = new Object[]{"Jan", 10900, 43900, 5000, 1700};
        data[1] = new Object[]{"Feb", 10900, 43900, null, 18900};
        data[2] = new Object[]{"Mar", 10900, 43900, null, 17200};
        data[3] = new Object[]{"Apr", 10900, 43900, 5000, 700};

        String[] columns = new String[]{"Month paid", "Netflix", "ICA Matkasse", "Nintendo", "Parkering"};

        TableModel model = new DefaultTableModel(data, columns);

        SpreadSheet ss = SpreadSheet.createEmpty(model);

        // Set background color on specific cell
        MutableCell mc = ss.getSheet(0).getCellAt("A1");
        mc.setBackgroundColor(new Color(1, 100, 50));

        MutableCell mc2 = ss.getSheet(0).getCellAt("B5");
//        mc.getFormula()

        final File file = new File("c:/temp/testdata/temperature.ods");
        ss.saveAs(file);
        OOUtils.open(file);
    }
}
