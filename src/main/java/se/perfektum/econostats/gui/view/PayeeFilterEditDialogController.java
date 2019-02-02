package se.perfektum.econostats.gui.view;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.perfektum.econostats.gui.model.PayeeFilter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class PayeeFilterEditDialogController {

    final Logger LOGGER = LoggerFactory.getLogger(PayeeFilterEditDialogController.class);

    @FXML
    private ListView<String> transactionNames;
    @FXML
    private ListView<String> payees;
    @FXML
    private ListView<String> excludedPayees;
    @FXML
    private TextField alias;
    @FXML
    private TextField customEntry;

    private PayeeFilter payeeFilter;

    private Stage dialogStage;
    private boolean saveClicked = false;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        transactionNames.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
        payees.setItems(payeeFilter.payeesProperty());
        excludedPayees.setItems(payeeFilter.excludedPayeesProperty());
        LOGGER.debug(String.format("PayeeFilter new/edit dialog initiated. Setting filter parameters...\nalias: %s\npayees: %s\nexcluded payees: %s", alias.getText(), payees.getItems(), excludedPayees.getItems()));
    }

    /**
     * Returns true if the user clicked Save, false otherwise.
     *
     * @return
     */
    public boolean isSaveClicked() {
        return saveClicked;
    }

    /**
     * Called when the user clicks Save.
     */
    @FXML
    private void handleSave() {
        if (isInputValid()) {
            LOGGER.debug("Saving PayeeFilter...");
            payeeFilter.setPayees(payees.getItems());
            payeeFilter.setExcludePayees(excludedPayees.getItems());
            payeeFilter.setAlias(alias.getText());

            saveClicked = true;
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
        LOGGER.debug(String.format("Adding payee(s)"));
        payees.setItems(FXCollections.observableArrayList(
                Stream.concat(
                        payees.getItems().stream(),
                        transactionNames.getSelectionModel().getSelectedItems().stream()
                )
                        .distinct()
                        .collect(Collectors.toList())));
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
    private void handleAddExcludePayee() {
        LOGGER.debug(String.format("Adding excluded payee(s)"));
        excludedPayees.setItems(FXCollections.observableArrayList(
                Stream.concat(
                        excludedPayees.getItems().stream(),
                        transactionNames.getSelectionModel().getSelectedItems().stream()
                )
                        .distinct()
                        .collect(Collectors.toList())));
    }

    /**
     * Called when the user clicks Remove Exclude Payee.
     */
    @FXML
    private void handleRemoveExcludePayee() {
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
        if (!payees.getItems().stream().anyMatch(s -> s.equalsIgnoreCase(customEntry.getText()))
                && !excludedPayees.getItems().stream().anyMatch(s -> s.equalsIgnoreCase(customEntry.getText()))) {
            entry.getItems().add(customEntry.getText());
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Duplicate Entry Error");
            alert.setHeaderText("The payee you are trying to add\nalready exists in the list of payees or excluded payees!");

            alert.showAndWait();
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
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
}
