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
import javafx.stage.Stage;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import se.perfektum.econostats.EconoStats;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.gui.model.PayeeFilter;
import se.perfektum.econostats.gui.view.PayeeFilterOverviewController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EnableConfigurationProperties
public class EconoStatsMain extends Application {

    private static EconoStats econoStats;
    private Stage primaryStage;
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

    public ObservableList<PayeeFilter> getPayeeFilter() {
        return payeeFilters;
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
     * Shows the person overview inside the root layout.
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
     * Returns the data as an observable list of Persons.
     *
     * @return
     */
    public ObservableList<PayeeFilter> getPayeeFilters() {
        return payeeFilters;
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
