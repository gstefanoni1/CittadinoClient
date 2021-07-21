import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/mainLayout.fxml"));
        primaryStage.setTitle("Vaccinazioni Cittadini");
        primaryStage.setScene(new Scene(root, 500, 300));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public MainApp(){}

    public static void main(String[] args) {
        launch(args);
    }
}