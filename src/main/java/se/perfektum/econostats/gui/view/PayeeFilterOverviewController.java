package se.perfektum.econostats.gui.view;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    // Reference to the main application.
    private EconoStatsMain econoStatsMain;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public PayeeFilterOverviewController() {
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param econoStatsMain
     */
    public void setEconoStatsMain(EconoStatsMain econoStatsMain) {
        LOGGER.debug("Setting main reference");
        this.econoStatsMain = econoStatsMain;

        LOGGER.debug("Adding observable list data to payeeFilterTable");
        // Add observable list data to the table
        payeeFilterTable.setItems(this.econoStatsMain.getPayeeFilters());
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
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
        payeeFilterTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPayeeFilterDetails(newValue));
    }

    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new PayeeFilter.
     */
    @FXML
    private void handleNewPayeeFilter() {
        PayeeFilter tempPayeeFilter = new PayeeFilter();
        boolean okClicked = econoStatsMain.showPayeeFilterEditDialog(tempPayeeFilter);
        if (okClicked) {
            LOGGER.debug(String.format("Saving new PayeeFilter: %s", tempPayeeFilter.toString()));
            econoStatsMain.getPayeeFilters().add(tempPayeeFilter);
        }
    }

    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected PayeeFilter.
     */
    @FXML
    private void handleEditPayeeFilter() {
        PayeeFilter selectedPayeeFilter = payeeFilterTable.getSelectionModel().getSelectedItem();
        if (selectedPayeeFilter != null) {
            boolean okClicked = econoStatsMain.showPayeeFilterEditDialog(selectedPayeeFilter);
            if (okClicked) {
                LOGGER.debug(String.format("Saving edited PayeeFilter: %s", selectedPayeeFilter.toString()));
                showPayeeFilterDetails(selectedPayeeFilter);
            }

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
        ButtonType result = MessageHandler.showYesNoDialog("Save Payee Filters to Google Drive?");
        if (result == ButtonType.YES) {
            LOGGER.debug(String.format("Saving %d Payee Filters to Google Drive", payeeFilterTable.getItems().size()));
            //TODO: Implement saving Payee Filters to Google Drive here!
        }
    }

    private void showPayeeFilterDetails(PayeeFilter payeeFilter) {
        if (payeeFilter != null) {
            LOGGER.debug("Populating list of payeeFilters");
            payees.setMouseTransparent(true);
            payees.setFocusTraversable(false);
            excludedPayees.setMouseTransparent(true);
            excludedPayees.setFocusTraversable(false);
            // Fill payeeFilter list  with info from the payeeFilter object.
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
}