package se.perfektum.econostats.gui.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.perfektum.econostats.gui.model.PayeeFilter;
import se.perfektum.econostats.gui.view.common.MessageHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PayeeFilterEditDialogController {

    final Logger LOGGER = LoggerFactory.getLogger(PayeeFilterEditDialogController.class);

    private static final String DEFAULT_CONTROL_INNER_BACKGROUND = "derive(-fx-base,80%)";
    private static final String HIGHLIGHTED_GREEN_CONTROL_INNER_BACKGROUND = "derive(PALEGREEN, 50%)";
    private static final String HIGHLIGHTED_RED_CONTROL_INNER_BACKGROUND = "derive(SALMON, 50%)";

    @FXML
    private ListView<String> transactionNames;
    @FXML
    private ListView<String> payees;
    @FXML
    private ListView<String> excludedPayees;
    @FXML
    private TextArea alias;
    @FXML
    private TextField customEntry;
    @FXML
    private CheckBox active;

    private PayeeFilter payeeFilter;
    private List<String> includedPayeeFilters;
    private List<String> excludedPayeeFilters;
    private Stage dialogStage;
    private boolean okClicked = false;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        transactionNames.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        transactionNames.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                            setStyle("-fx-control-inner-background: " + DEFAULT_CONTROL_INNER_BACKGROUND + ";");
                        } else {
                            setText(item);
                            if (includedPayeeFilters.stream().anyMatch(i -> i.contains(item))) {
                                setStyle("-fx-control-inner-background: " + HIGHLIGHTED_GREEN_CONTROL_INNER_BACKGROUND + ";");
                            } else if (excludedPayeeFilters.stream().anyMatch(i -> i.contains(item))) {
                                setStyle("-fx-control-inner-background: " + HIGHLIGHTED_RED_CONTROL_INNER_BACKGROUND + ";");
                            } else {
                                setStyle("-fx-control-inner-background: " + DEFAULT_CONTROL_INNER_BACKGROUND + ";");
                            }
                        }
                    }
                };
            }
        });
    }

    public void setIncludedPayees(ObservableList<PayeeFilter> payeeFilters) {
        includedPayeeFilters = new ArrayList<>();
        excludedPayeeFilters = new ArrayList<>();
        includedPayeeFilters.addAll(payeeFilters.stream().flatMap(d -> d.getPayees().stream()).distinct().collect(Collectors.toList()));
        excludedPayeeFilters.addAll(payeeFilters.stream().flatMap(d -> d.getExcludedPayees().stream()).distinct().collect(Collectors.toList()));
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the transactionNames to be edited in the dialog.
     *
     * @param transactionNames
     */
    public void setTransactionNames(List<String> transactionNames) {
        this.transactionNames.setItems(FXCollections.observableArrayList(transactionNames));
    }

    /**
     * Sets the payeeFilters to be edited in the dialog.
     *
     * @param payeeFilter
     */
    public void setPayeeFilter(PayeeFilter payeeFilter) {
        this.payeeFilter = payeeFilter;

        alias.setText(payeeFilter.aliasProperty().getValue());
        active.setSelected(payeeFilter.activeProperty().getValue());
        payees.setItems(payeeFilter.payeesProperty());
        excludedPayees.setItems(payeeFilter.excludedPayeesProperty());
        LOGGER.debug(String.format("PayeeFilter new/edit dialog initiated. Setting filter parameters...\nalias: %s\npayees: %s\nexcluded payees: %s", alias.getText(), active, payees.getItems(), excludedPayees.getItems()));
    }

    /**
     * Returns true if the user clicked Ok, false otherwise.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks Ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            LOGGER.debug("Setting new payeeFilter values");
            payeeFilter.setAlias(alias.getText());
            payeeFilter.setActive(active.isSelected());
            payeeFilter.setPayees(payees.getItems());
            payeeFilter.setExcludedPayees(excludedPayees.getItems());

            okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Called when the user clicks Add Payee.
     */
    @FXML
    private void handleAddPayee() {
        if (Collections.disjoint(excludedPayees.getItems(), transactionNames.getSelectionModel().getSelectedItems())) {
            LOGGER.debug(String.format("Adding payee(s)"));
            payees.setItems(FXCollections.observableArrayList(
                    Stream.concat(
                            payees.getItems().stream(),
                            transactionNames.getSelectionModel().getSelectedItems().stream()
                    )
                            .distinct()
                            .collect(Collectors.toList())));
        } else {
            MessageHandler.showError("Duplicate Entry Error", "The item you are trying to add\nalready exists in the list of payees or excluded payees!");
        }
    }

    /**
     * Called when the user clicks Remove Payee.
     */
    @FXML
    private void handleRemovePayee() {
        LOGGER.debug(String.format("Removing payee(s)"));
        payees.getItems().remove(payees.getSelectionModel().getSelectedItem());
    }

    /**
     * Called when the user clicks Add Exclude Payee.
     */
    @FXML
    private void handleAddExcludedPayee() {
        if (Collections.disjoint(payees.getItems(), transactionNames.getSelectionModel().getSelectedItems())) {
            LOGGER.debug(String.format("Adding excluded payee(s)"));
            excludedPayees.setItems(FXCollections.observableArrayList(
                    Stream.concat(
                            excludedPayees.getItems().stream(),
                            transactionNames.getSelectionModel().getSelectedItems().stream()
                    )
                            .distinct()
                            .collect(Collectors.toList())));
        } else {
            MessageHandler.showError("Duplicate Entry Error", "The item you are trying to add\nalready exists in the list of payees or excluded payees!");
        }
    }

    /**
     * Called when the user clicks Remove Excluded Payee.
     */
    @FXML
    private void handleRemoveExcludedPayee() {
        LOGGER.debug(String.format("Removing excluded payee(s)"));
        excludedPayees.getItems().remove(excludedPayees.getSelectionModel().getSelectedItem());
    }

    /**
     * Called when the user clicks Add as Payee.
     */
    @FXML
    private void handleAddCustomPayee() {
        LOGGER.debug(String.format("Adding custom payee"));
        addCustomEntry(payees);
    }

    /**
     * Called when the user clicks Add as Exclusion.
     */
    @FXML
    private void handleAddCustomExclusion() {
        LOGGER.debug(String.format("Adding custom excluded payee"));
        addCustomEntry(excludedPayees);
    }

    private void addCustomEntry(ListView<String> entry) {
        if (customEntry.getText().isEmpty()) {
            MessageHandler.showError("Empty Entry Error", "Please enter a payee entry in the text field before adding!");
        }
        if (!payees.getItems().stream().anyMatch(s -> s.equalsIgnoreCase(customEntry.getText()))
                && !excludedPayees.getItems().stream().anyMatch(s -> s.equalsIgnoreCase(customEntry.getText()))) {
            entry.getItems().add(customEntry.getText());
        } else {
            MessageHandler.showError("Duplicate Entry Error", "The item you are trying to add\nalready exists in the list of payees or excluded payees!");
        }
    }

    /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        LOGGER.debug("Validating user input");
        String errorMessage = "";

        if (alias.getText() == null || alias.getText().length() == 0) {
            errorMessage += "Alias must be provided!\n";
        }
        if (payees == null || payees.getItems().size() == 0) {
            errorMessage += "You got to add at least one payee!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            MessageHandler.showError("Invalid Fields", "Please correct invalid fields!");
            return false;
        }
    }
}
