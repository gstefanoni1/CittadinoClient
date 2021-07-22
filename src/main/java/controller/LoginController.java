package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


import java.io.IOException;

public class LoginController {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label labelIndietro;


    public static String client = "";

    public void loginRequest(MouseEvent mouseEvent) {
        if(!verificaCampi()) return;

        client = username.getText();

        indietro(mouseEvent);

    }

    public void registrazione(MouseEvent mouseEvent) {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../view/registrazioneLayout.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 450);
            Stage stage = new Stage();
            stage.setTitle("Registra Centro Vaccinale");
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

    public void indietro(MouseEvent mouseEvent) {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../view/mainLayout.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 300);
            Stage stage = new Stage();
            stage.setTitle("Vaccinazioni Cittadini");
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
    private boolean verificaCampi() {
        if(username.getText().equals("") || password.getText().equals("")){
            return false;
        }

        return true;
    }
}
