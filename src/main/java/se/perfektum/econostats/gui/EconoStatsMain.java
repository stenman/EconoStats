package se.perfektum.econostats.gui;

import java.io.IOException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import se.perfektum.econostats.EconoStatsController;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.gui.model.PayeeFilter;
import se.perfektum.econostats.gui.view.EconoStatsOverviewController;
import se.perfektum.econostats.gui.view.PayeeFilterEditDialogController;
import se.perfektum.econostats.gui.view.PayeeFilterOverviewController;

@EnableConfigurationProperties
public class EconoStatsMain extends Application {

    static final Logger LOGGER = LoggerFactory.getLogger(EconoStatsMain.class);

    private static EconoStatsController econoStatsController;
    private static Stage primaryStage;
    private TabPane rootLayout;

    public static void main(String args[]) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml")) {
            econoStatsController = context.getBean(EconoStatsController.class);
        }
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
            controller.setReferences(this, econoStatsController);
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
            controller.setReferences(this, econoStatsController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a dialog to edit details for the specified PayeeFilter. If the user clicks Save, the changes are saved into the provided payeeFilter
     * object and true is returned.
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

            controller.setIncludedPayees(econoStatsController.getPayeeFilters());

            LOGGER.debug("Generating distinct set of Account Transaction Names");
            ObservableList<String> transactionNames = FXCollections
                    .observableArrayList(econoStatsController.getAccountTransactions().stream().map(AccountTransaction::getHeader).distinct().sorted().collect(Collectors.toList()));
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
     * Returns the main stage.
     *
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
