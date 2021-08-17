import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class GUIStarter extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/mainLayout.fxml"));
        primaryStage.getIcons().add(new Image(String.valueOf(getClass().getResource("img/icon.png"))));
        primaryStage.setTitle("Vaccinazioni Cittadini");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public GUIStarter(){}

    public static void exec(String[] args) {
        launch(args);
    }
}