package se.perfektum.econostats.gui.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.perfektum.econostats.EconoStatsController;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.gui.EconoStatsMain;
import se.perfektum.econostats.utils.SystemUtils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class EconoStatsOverviewController {
    final Logger LOGGER = LoggerFactory.getLogger(EconoStatsOverviewController.class);

    @FXML
    private TextArea eventLog;
    @FXML
    private TextField csvPath;

    // Reference to the main application.
    private EconoStatsMain econoStatsMain;
    private EconoStatsController econoStatsController;

    /**
     * Default constructor. The constructor is called before the initialize() method.
     */
    public EconoStatsOverviewController() {
    }

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param econoStatsMain
     */
    public void setReferences(EconoStatsMain econoStatsMain, EconoStatsController econoStatsController) {
        LOGGER.debug("Setting main reference");
        this.econoStatsMain = econoStatsMain;
        this.econoStatsController = econoStatsController;
        this.csvPath.setText(this.econoStatsController.getCsvFilePath());
    }

    // TODO: Fix csvFilePath property. It should be configurable! Also, use a config/property-util instead of springs crap...
    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open bank csv file");
        if (SystemUtils.isWindows()) {
            File defaultDirectory = new File(econoStatsController.getCsvPath());
            fileChooser.setInitialDirectory(defaultDirectory);
        }
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(econoStatsMain.getPrimaryStage());
        if (selectedFile != null) {
            this.csvPath.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleLoadFromDisk() {
        List<AccountTransaction> accountTransactions = null;
        File file = new File(csvPath.getText());
        if (file.exists() && !file.isDirectory()) {
            accountTransactions = econoStatsController.fetchAccountTransactions(csvPath.getText());
            econoStatsController.setAccountTransactions(accountTransactions);
        }
        if (accountTransactions != null && accountTransactions.size() > 0) {
            LOGGER.debug(String.format("Loaded %s Account Transactions from disk", accountTransactions.size()));
            eventLog.appendText(String.format("Loaded %s Account Transactions from disk\n", accountTransactions.size()));
        } else {
            LOGGER.debug("Failed loading Account Transactions from disk");
            eventLog.appendText("Failed loading Account Transactions from disk. Are you sure the file exists and has the correct format?\n");
        }
    }

    @FXML
    private void handleGenerateRecurringTransactions() {
        econoStatsController.generateRecurringTransactions();
        eventLog.appendText(String.format("Done! %s Account Transactions processed over %s active Payee Filter(s)!\n", econoStatsController.getAccountTransactions().size(),
                econoStatsController.getPayeeFilters().stream().filter(f -> f.isActive()).collect(Collectors.toList()).size()));
    }
}
