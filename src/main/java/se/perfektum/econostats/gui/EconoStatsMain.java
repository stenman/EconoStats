package se.perfektum.econostats.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import se.perfektum.econostats.EconoStats;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.gui.model.PayeeFilter;
import se.perfektum.econostats.gui.view.EconoStatsOverviewController;
import se.perfektum.econostats.gui.view.PayeeFilterEditDialogController;
import se.perfektum.econostats.gui.view.PayeeFilterOverviewController;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@EnableConfigurationProperties
public class EconoStatsMain extends Application {

    static final Logger LOGGER = LoggerFactory.getLogger(EconoStatsMain.class);

    private static EconoStats econoStats;
    private static Stage primaryStage;
    private TabPane rootLayout;

    private static ObservableList<PayeeFilter> payeeFilters = FXCollections.observableArrayList();
    private static ObservableList<AccountTransaction> accountTransactions = FXCollections.observableArrayList();


    public static void main(String args[]) {
        ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
        econoStats = (EconoStats) context.getBean("econoStats");

//        Use this to save local payeeFilters to Drive, until GUI works properly
//        To reset everything: delete EconoStats folder on drive and run tempSavePayeeFiltersToDrive + generateRecurringTransactions
//        econoStats.tempSavePayeeFiltersToDrive();
//        generateRecurringTransactions();

        // Get Payee Filters from Google Drive
        List<se.perfektum.econostats.domain.PayeeFilter> pfs = econoStats.getPayeeFilters();
        payeeFilters.addAll(pfs == null ? FXCollections.observableArrayList(Collections.emptyList()) : FXCollections.observableArrayList(PayeeFilter.convertFromDomain(pfs)));

        setupMockdata();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        EconoStatsMain.primaryStage = primaryStage;
        EconoStatsMain.primaryStage.setTitle("EconoStats - Choose Payee Filters");
        initRootLayout();
        showEconoStatsOverview();
        showPayeeFilterOverview();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            LOGGER.debug("Initiating root layout");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(EconoStatsMain.class.getResource("view/RootLayout.fxml"));
            rootLayout = loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            LOGGER.debug("Root layout initiated, showing primary stage");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the PayeeFilter overview inside the root layout.
     */
    public void showEconoStatsOverview() {
        try {
            LOGGER.debug("Initiating EconoStatsOverview");
            // Load EconoStats overview.
            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(EconoStatsMain.class.getResource("view/EconoStatsOverview.fxml"));
            AnchorPane econoStatsOverview = loader.load();

            // Set EconoStats overview into the root layout.
            rootLayout.getTabs().get(0).setContent(econoStatsOverview);

            // Give the controller access to the main app.
            EconoStatsOverviewController controller = loader.getController();
            controller.setEconoStatsMain(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the PayeeFilter overview inside the root layout.
     */
    public void showPayeeFilterOverview() {
        try {
            LOGGER.debug("Initiating PayeeFilterOverview");
            // Load payeeFilter overview.
            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(EconoStatsMain.class.getResource("view/PayeeFilterOverview.fxml"));
            AnchorPane payeeFilterOverview = loader.load();

            // Set payeeFilter overview into the root layout.
            rootLayout.getTabs().get(1).setContent(payeeFilterOverview);

            // Give the controller access to the main app.
            PayeeFilterOverviewController controller = loader.getController();
            controller.setEconoStatsMain(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a dialog to edit details for the specified PayeeFilter. If the user
     * clicks Save, the changes are saved into the provided payeeFilter object and true
     * is returned.
     *
     * @param payeeFilter the PayeeFilter object to be edited
     * @return true if the user clicked Save, false otherwise.
     */
    public boolean showPayeeFilterEditDialog(PayeeFilter payeeFilter) {
        try {
            LOGGER.debug("Initiating PayeeFilterEditDialog");
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(EconoStatsMain.class.getResource("view/PayeeFilterEditDialog.fxml"));
            AnchorPane page = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Payee Filter");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the payeeFilter into the controller.
            PayeeFilterEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPayeeFilter(payeeFilter);
            LOGGER.debug("Generating distinct set of Account Transaction Names");
            ObservableList<String> transactionNames = FXCollections.observableArrayList(accountTransactions.stream().map(AccountTransaction::getName).distinct().collect(Collectors.toList()));
            controller.setTransactionNames(transactionNames);

            LOGGER.debug("PayeeFilterEditDialog initiated, showing dialog");
            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Access method for PayeeFilters.
     *
     * @return an observable list of PayeeFilters
     */
    public ObservableList<PayeeFilter> getPayeeFilters() {
        return payeeFilters;
    }

    /**
     * Access method for AccountTransactions.
     *
     * @return an observable list of AccountTransactions
     */
    public static ObservableList<AccountTransaction> getAccountTransactions() {
        return accountTransactions;
    }

    /**
     * Returns the main stage.
     *
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    //Unused until GUI is ready
    private List<Button> setButtons(Stage primaryStage, List<se.perfektum.econostats.domain.PayeeFilter> payeeFilters, List<AccountTransaction> accountTransactions) {
        Button btn = new Button();
        btn.setText("Create Recurring Transactions!");
        btn.setOnAction(event -> {
            try {
                if (payeeFilters != null && accountTransactions != null) {
                    econoStats.generateRecurringTransactions(payeeFilters, accountTransactions);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Get outta town! No filters or transactions! =(", ButtonType.OK);
                    alert.showAndWait();
                    primaryStage.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                primaryStage.close();
            }
        });
        return Arrays.asList(btn);
    }

    //Unused until GUI is ready
    private static void generateRecurringTransactions() {
        List<se.perfektum.econostats.domain.PayeeFilter> payeeFilters = econoStats.getPayeeFilters();
        List<AccountTransaction> accountTransactions = econoStats.getAccountTransactions();
        try {
            if (payeeFilters != null && accountTransactions != null) {
                econoStats.generateRecurringTransactions(payeeFilters, accountTransactions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        ObservableList<String> transactionAliases = FXCollections.observableArrayList(accountTransactions.stream().map(AccountTransaction::getName).collect(Collectors.toList()));
//        List<Button> buttons = setButtons(primaryStage, payeeFilters, accountTransactions);
    }

    private static void setupMockdata() {
//        List<String> payees1 = new ArrayList<>();
//        payees1.addAll(Arrays.asList("Payee A", "Payee B", "Payee C"));
//        List<String> excludedPayees1 = new ArrayList<>();
//        excludedPayees1.addAll(Arrays.asList("excludedPayee A", "excludedPayee B", "excludedPayee C"));
//        PayeeFilter pf1 = new PayeeFilter(payees1, excludedPayees1, "Some Alias 1", true);
//        List<String> payees2 = new ArrayList<>();
//        payees2.addAll(Arrays.asList("Payee D", "Payee E", "Payee F"));
//        List<String> excludedPayees2 = new ArrayList<>();
//        excludedPayees2.addAll(Arrays.asList("excludedPayee D", "excludedPayee E", "excludedPayee f"));
//        PayeeFilter pf2 = new PayeeFilter(payees2, excludedPayees2, "Some Alias 2", false);
//        payeeFilters.addAll(Arrays.asList(pf1, pf2));

        AccountTransaction t1 = new AccountTransaction.Builder().name("Transaction 1").build();
        AccountTransaction t2 = new AccountTransaction.Builder().name("Transaction 2").build();
        AccountTransaction t3 = new AccountTransaction.Builder().name("Transaction 1 REFUND").build();
        AccountTransaction t4 = new AccountTransaction.Builder().name("Transaction 2").build();
        AccountTransaction t5 = new AccountTransaction.Builder().name("Transaction 4").build();
        accountTransactions.addAll(Arrays.asList(t1, t2, t3, t4, t5));
    }

}
