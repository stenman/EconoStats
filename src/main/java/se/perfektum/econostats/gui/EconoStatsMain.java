package se.perfektum.econostats.gui;

import javafx.application.Application;
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
import se.perfektum.econostats.domain.PayeeFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@EnableConfigurationProperties
public class EconoStatsMain extends Application {

    private static EconoStats econoStats;
    private Stage primaryStage;
    private BorderPane rootLayout;

    public static void main(String args[]) {
        ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
        econoStats = (EconoStats) context.getBean("econoStats");

//        Use this to save local payeeFilters to Drive, until GUI works properly
//                econoStats.tempSavePayeeFiltersToDrive();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("EconoStats - Choose Payee Filters");
        initRootLayout();
        showPersonOverview();
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
    public void showPersonOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(EconoStatsMain.class.getResource("view/FilterOverview.fxml"));
            AnchorPane personOverview = loader.load();

            rootLayout.setCenter(personOverview);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Unused until GUI is ready
    private List<Button> setButtons(Stage primaryStage, List<PayeeFilter> payeeFilters, List<AccountTransaction> accountTransactions) {
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
        List<PayeeFilter> payeeFilters = econoStats.getPayeeFilters();
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
}
