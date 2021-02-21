package se.perfektum.econostats.gui.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import se.perfektum.econostats.EconoStatsController;
import se.perfektum.econostats.dao.googledrive.MimeTypes;
import se.perfektum.econostats.gui.EconoStatsMain;
import se.perfektum.econostats.gui.model.PayeeFilter;
import se.perfektum.econostats.gui.view.common.MessageHandler;

public class PayeeFilterOverviewController {

    final Logger LOGGER = LoggerFactory.getLogger(PayeeFilterOverviewController.class);

    @FXML
    private TableView<PayeeFilter> payeeFilterTable;
    @FXML
    private TableColumn<PayeeFilter, String> aliasColumn;
    @FXML
    private TableColumn<PayeeFilter, Boolean> activeColumn;
    @FXML
    private ListView<String> payees;
    @FXML
    private ListView<String> excludedPayees;

    @FXML
    private Label aliasLabel;

    private static final String PAYEE_FILTERS_FILE_NAME = "payeeFilters.json";

    // Reference to the main application.
    private EconoStatsMain econoStatsMain;
    private EconoStatsController econoStatsController;

    /**
     * Default constructor. The constructor is called before the initialize() method.
     */
    public PayeeFilterOverviewController() {
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param econoStatsMain
     */
    public void setReferences(EconoStatsMain econoStatsMain, EconoStatsController econoStatsController) {
        LOGGER.debug("Setting references");
        this.econoStatsMain = econoStatsMain;
        this.econoStatsController = econoStatsController;

        LOGGER.debug("Adding observable list data to payeeFilterTable");
        // Add observable list data to the table
        payeeFilterTable.setItems(this.econoStatsController.getPayeeFilters());
    }

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        payeeFilterTable.setEditable(true);

        aliasColumn.setCellValueFactory(cellData -> cellData.getValue().aliasProperty());
        activeColumn.setCellValueFactory(p -> p.getValue().activeProperty());
        activeColumn.setCellFactory(p -> new CheckBoxTableCell<>());

        // Clear payeeFilter details.
        showPayeeFilterDetails(null);

        // Listen for selection changes and show the payeeFilter details when changed.
        payeeFilterTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showPayeeFilterDetails(newValue));
    }

    /**
     * Called when the user clicks the new button. Opens a dialog to edit details for a new PayeeFilter.
     */
    @FXML
    private void handleNewPayeeFilter() {
        PayeeFilter tempPayeeFilter = new PayeeFilter();
        if (isTransactionsFileLoaded()) {
            boolean okClicked = econoStatsMain.showPayeeFilterEditDialog(tempPayeeFilter);
            if (okClicked) {
                LOGGER.debug(String.format("Saving new PayeeFilter: %s", tempPayeeFilter.toString()));
                econoStatsController.getPayeeFilters().add(tempPayeeFilter);
            }
        } else {
            MessageHandler.showWarning("No Transaction File Loaded", "No Transaction File Loaded", "Please load a transactions file (csv) containing your financial transactions.");
        }
    }

    /**
     * Called when the user clicks the edit button. Opens a dialog to edit details for the selected PayeeFilter.
     */
    @FXML
    private void handleEditPayeeFilter() {
        PayeeFilter selectedPayeeFilter = payeeFilterTable.getSelectionModel().getSelectedItem();
        if (isTransactionsFileLoaded() && selectedPayeeFilter != null) {
            boolean okClicked = econoStatsMain.showPayeeFilterEditDialog(selectedPayeeFilter);
            if (okClicked) {
                LOGGER.debug(String.format("Saving edited PayeeFilter: %s", selectedPayeeFilter.toString()));
                showPayeeFilterDetails(selectedPayeeFilter);
            }

        } else if (!isTransactionsFileLoaded()) {
            MessageHandler.showWarning("No Transaction File Loaded", "No Transaction File Loaded", "Please load a transactions file (csv) containing your financial transactions.");
        } else {
            MessageHandler.showWarning("No Selection", "No Payee Filter Selected", "Please select a Payee Filter in the table.");
        }
    }

    /**
     * Called when the user clicks the delete button. Opens a dialog confirmation dialog.
     */
    @FXML
    private void handleDeletePayeeFilter() {
        int selectedIndex = payeeFilterTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            ButtonType result = MessageHandler.showYesNoDialog("Delete " + payeeFilterTable.getItems().get(selectedIndex).getAlias().replace("\n", " ") + " ?");

            if (result == ButtonType.YES) {
                LOGGER.debug(String.format("Removing [%s] from payeeFilterTable", payeeFilterTable.getItems().get(selectedIndex).getAlias()));
                payeeFilterTable.getItems().remove(selectedIndex);
            }
        } else {
            MessageHandler.showWarning("No Selection", "No Payee Filter Selected", "Please select a Payee Filter in the table.");
        }
    }

    @FXML
    private void handleSave() {
        ButtonType save = MessageHandler.showYesNoDialog("Save Payee Filters to Google Drive?");
        if (save == ButtonType.YES) {
            LOGGER.debug("Checking for existing Payee Filters on Google Drive...");
            // TODO: PAYEE_FILTERS_FILE_NAME is a property, get it from appProperties
            // (somehow)!
            if (econoStatsController.getFileId(PAYEE_FILTERS_FILE_NAME, MimeTypes.APPLICATION_JSON.toString()) != null) {
                ButtonType overwrite = MessageHandler.showYesNoDialog("Payee Filter already exists on Google Drive. Overwrite?");
                if (overwrite == ButtonType.YES) {
                    savePayeeFilters();
                }
            } else {
                savePayeeFilters();
            }
        }
    }

    private void showPayeeFilterDetails(PayeeFilter payeeFilter) {
        if (payeeFilter != null) {
            LOGGER.debug("Populating list of payeeFilters");
            payees.setFocusTraversable(false);
            excludedPayees.setFocusTraversable(false);
            // Fill payeeFilter list with info from the payeeFilter object.
            aliasLabel.setText(payeeFilter.getAlias().replace("\n", " "));
            payees.setItems(payeeFilter.payeesProperty());
            excludedPayees.setItems(payeeFilter.excludedPayeesProperty());
            // Save edited values to table
            payeeFilterTable.refresh();
        } else {
            LOGGER.debug("Resetting list of payeeFilters");
            // payeeFilter is null, remove all the text.
            aliasLabel.setText("");
            payees.setItems(FXCollections.observableArrayList());
            excludedPayees.setItems(FXCollections.observableArrayList());
        }
    }

    private void savePayeeFilters() {
        LOGGER.debug(String.format("Saving %d Payee Filters to Google Drive", payeeFilterTable.getItems().size()));
        econoStatsController.savePayeeFilters(PayeeFilter.convertToDomain(econoStatsController.getPayeeFilters()));
    }

    private boolean isTransactionsFileLoaded() {
        return !econoStatsController.getAccountTransactions().isEmpty();
    }
}