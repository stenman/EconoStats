package se.perfektum.econostats.gui.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import se.perfektum.econostats.gui.EconoStatsMain;
import se.perfektum.econostats.gui.model.PayeeFilter;

public class PayeeFilterOverviewController {
    @FXML
    private TableView<PayeeFilter> payeeFilterTable;
    @FXML
    private TableColumn<PayeeFilter, String> aliasColumn;

    @FXML
    private Label aliasLabel;
    @FXML
    private Label payeesLabel;
    @FXML
    private Label excludedPayeesLabel;

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
        // Initialize the person table with the two columns.
        aliasColumn.setCellValueFactory(cellData -> cellData.getValue().aliasProperty());
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
}