package se.perfektum.econostats.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import se.perfektum.econostats.EconoStats;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.gui.model.PayeeFilter;
import se.perfektum.econostats.gui.view.PayeeFilterEditDialogController;
import se.perfektum.econostats.gui.view.PayeeFilterOverviewController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EnableConfigurationProperties
public class EconoStatsMain extends Application {

    private static EconoStats econoStats;
    private static Stage primaryStage;
    private BorderPane rootLayout;

    private static ObservableList<PayeeFilter> payeeFilters = FXCollections.observableArrayList();

    public static void main(String args[]) {
        ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
        econoStats = (EconoStats) context.getBean("econoStats");

//        Use this to save local payeeFilters to Drive, until GUI works properly
//                econoStats.tempSavePayeeFiltersToDrive();

        setupPayeeFiltersMockdata();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("EconoStats - Choose Payee Filters");
        initRootLayout();
        showPayeeFilterOverview();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(EconoStatsMain.class.getResource("view/RootLayout.fxml"));
            rootLayout = loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the PayeeFilter overview inside the root layout.
     */
    public void showPayeeFilterOverview() {
        try {
            // Load payeeFilter overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(EconoStats.class.getResource("gui/view/PayeeFilterOverview.fxml"));
            AnchorPane payeeFilterOverview = loader.load();

            // Set payeeFilter overview into the center of root layout.
            rootLayout.setCenter(payeeFilterOverview);

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
    public static boolean showPayeeFilterEditDialog(PayeeFilter payeeFilter) {
        try {
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

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isSaveClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the data as an observable list of PayeeFilters.
     *
     * @return
     */
    public static ObservableList<PayeeFilter> getPayeeFilters() {
        return payeeFilters;
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
    private void generateRecurringTransactions() {
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

    private static void setupPayeeFiltersMockdata() {
        List<String> payees1 = new ArrayList<>();
        payees1.addAll(Arrays.asList("Payee A", "Payee B", "Payee C"));
        List<String> excludedPayees1 = new ArrayList<>();
        excludedPayees1.addAll(Arrays.asList("excludedPayee A", "excludedPayee B", "excludedPayee C"));
        PayeeFilter pf1 = new PayeeFilter(payees1, excludedPayees1, "Some Alias 1");
        List<String> payees2 = new ArrayList<>();
        payees2.addAll(Arrays.asList("Payee D", "Payee E", "Payee F"));
        List<String> excludedPayees2 = new ArrayList<>();
        excludedPayees2.addAll(Arrays.asList("excludedPayee D", "excludedPayee E", "excludedPayee f"));
        PayeeFilter pf2 = new PayeeFilter(payees2, excludedPayees2, "Some Alias 2");
        payeeFilters.addAll(Arrays.asList(pf1, pf2));
    }

}
