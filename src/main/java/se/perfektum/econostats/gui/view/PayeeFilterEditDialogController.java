package se.perfektum.econostats.gui.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import se.perfektum.econostats.gui.model.PayeeFilter;

public class PayeeFilterEditDialogController {

    @FXML
    private ListView<String> accountTransactions;
    @FXML
    private ListView<String> payees;
    @FXML
    private ListView<String> excludedPayees;
    @FXML
    private TextField alias;
    @FXML
    private TextField customEntry;

    @FXML
    private Label accountTransationsLabel;
    @FXML
    private Label payeesLabel;
    @FXML
    private Label excludedPayeesLabel;

    private PayeeFilter payeeFilter;

    private Stage dialogStage;
    private boolean saveClicked = false;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
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
     * Sets the payeeFilter to be edited in the dialog.
     *
     * @param payeeFilter
     */
    public void setPayeeFilter(PayeeFilter payeeFilter) {
        this.payeeFilter = payeeFilter;

        payees.setItems(payeeFilter.payeesProperty());
        excludedPayees.setItems(payeeFilter.excludedPayeesProperty());
        alias.setText(payeeFilter.aliasProperty().getValue());
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
        payees.setItems(accountTransactions.getSelectionModel().getSelectedItems());
    }

    /**
     * Called when the user clicks Remove Payee.
     */
    @FXML
    private void handleRemovePayee() {
        payees.setItems(FXCollections.observableArrayList());
    }

    /**
     * Called when the user clicks Add Exclude Payee.
     */
    @FXML
    private void handleAddExcludePayee() {
        dialogStage.close();
    }

    /**
     * Called when the user clicks Remove Exclude Payee.
     */
    @FXML
    private void handleRemoveExcludePayee() {
        dialogStage.close();
    }

    /**
     * Called when the user clicks Add as Payee.
     */
    @FXML
    private void handleAddAsPayee() {
        dialogStage.close();
    }

    /**
     * Called when the user clicks Add as Exclusion.
     */
    @FXML
    private void handleAddAsExclusion() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid
     */
    private boolean isInputValid() {
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