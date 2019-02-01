package se.perfektum.econostats.gui.view;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import se.perfektum.econostats.gui.EconoStatsMain;
import se.perfektum.econostats.gui.model.PayeeFilter;

import java.lang.reflect.InvocationTargetException;

public class PayeeFilterOverviewController {
    @FXML
    private TableView<PayeeFilter> payeeFilterTable;
    @FXML
    private TableColumn<PayeeFilter, String> aliasColumn;
    @FXML
    private ListView<String> payeeColumn;
    @FXML
    private ListView<String> excludedPayeeColumn;

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
            payeeColumn.setItems(payeeFilter.payeesProperty());
            excludedPayeeColumn.setItems(payeeFilter.excludedPayeesProperty());
        } else {
            // payeeFilter is null, remove all the text.
            aliasLabel.setText("");
            payeeColumn.setItems(FXCollections.observableArrayList());
            excludedPayeeColumn.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    private void handleDeletePayeeFilter() {
        int selectedIndex = payeeFilterTable.getSelectionModel().getSelectedIndex();
        try {
            payeeFilterTable.getItems().remove(selectedIndex);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                return;
            }
        }
    }
}