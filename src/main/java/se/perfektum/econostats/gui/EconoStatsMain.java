package se.perfektum.econostats.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import se.perfektum.econostats.EconoStats;

@EnableConfigurationProperties
public class EconoStatsMain extends Application {

    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
        EconoStats econoStats = (EconoStats) context.getBean("econoStats");

        Button btn = new Button();
        btn.setText("Create Recurring Transactions!");
        btn.setOnAction(event -> {
            try {
                econoStats.start();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                primaryStage.close();
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("EconoStats");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
