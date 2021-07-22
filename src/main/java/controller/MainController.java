package controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Button visualizzaCentri;
    @FXML
    private Button login;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (!(LoginController.client.equals(""))){
            login.setText("Disconnetti");
        }else{
            login.setText("Login");
        }
    }
    public void visualizzaInfoCentri(MouseEvent mouseEvent) {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../view/cercaVisualizzaLayout.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 400);
            Stage stage = new Stage();
            stage.setTitle("Visualizza Centro Vaccinale");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            Node source = (Node) mouseEvent.getSource();
            Stage thisStage = (Stage) source.getScene().getWindow();
            thisStage.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loginDisconnect(MouseEvent mouseEvent) {
        if (LoginController.client.equals("")){
            //Login
            Parent root;
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("../view/loginLayout.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 500, 300);
                Stage stage = new Stage();
                stage.setTitle("Login");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();

                Node source = (Node) mouseEvent.getSource();
                Stage thisStage = (Stage) source.getScene().getWindow();
                thisStage.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            //Disconnetti
            LoginController.client = "";
            login.setText("Login");
        }
    }



}
