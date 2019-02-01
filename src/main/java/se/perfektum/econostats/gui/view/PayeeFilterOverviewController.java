package se.perfektum.econostats.gui.view;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import se.perfektum.econostats.gui.EconoStatsMain;
import se.perfektum.econostats.gui.model.PayeeFilter;

public class PayeeFilterOverviewController {
    @FXML
    private TableView<PayeeFilter> payeeFilterTable;
    @FXML
    private TableColumn<PayeeFilter, String> aliasColumn;
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
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        aliasColumn.setCellValueFactory(cellData -> cellData.getValue().aliasProperty());

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
        boolean okClicked = EconoStatsMain.showPayeeFilterEditDialog(tempPayeeFilter);
        if (okClicked) {
            EconoStatsMain.getPayeeFilters().add(tempPayeeFilter);
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
            boolean okClicked = EconoStatsMain.showPayeeFilterEditDialog(selectedPayeeFilter);
            if (okClicked) {
                showPayeeFilterDetails(selectedPayeeFilter);
            }

        } else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(econoStatsMain.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Payee Filter Selected");
            alert.setContentText("Please select a Payee Filter in the table.");

            alert.showAndWait();
        }
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param econoStatsMain
     */
    public void setEconoStatsMain(EconoStatsMain econoStatsMain) {
        this.econoStatsMain = econoStatsMain;

        // Add observable list data to the table
        payeeFilterTable.setItems(econoStatsMain.getPayeeFilters());
    }

    private void showPayeeFilterDetails(PayeeFilter payeeFilter) {
        if (payeeFilter != null) {
            // Fill the labels with info from the payeeFilter object.
            aliasLabel.setText(payeeFilter.getAlias());
            payees.setItems(payeeFilter.payeesProperty());
            excludedPayees.setItems(payeeFilter.excludedPayeesProperty());
        } else {
            // payeeFilter is null, remove all the text.
            aliasLabel.setText("");
            payees.setItems(FXCollections.observableArrayList());
            excludedPayees.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    private void handleDeletePayeeFilter() {
        int selectedIndex = payeeFilterTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            payeeFilterTable.getItems().remove(selectedIndex);
        }
    }
}