package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class RegistrazioneController {

    @FXML
    private TextField id;
    @FXML
    private TextField nome;
    @FXML
    private TextField cognome;
    @FXML
    private TextField codFiscale;
    @FXML
    private TextField username;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField confPassword;
    @FXML
    private AnchorPane topPane;
    @FXML
    private AnchorPane bottomPane;


    public void indietro(MouseEvent mouseEvent) {
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
    }

    public void registraCittadino(MouseEvent mouseEvent) {
        if(!verificaCampi()){
            return;
        }
        //Alert con id da dare al cittadino
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informazioni cittadino: " + codFiscale.getText());
        alert.setHeaderText(null);
        alert.setContentText("ID vaccinazione: " + id + ". Registrazione completata");
        alert.showAndWait();

        indietro(mouseEvent);
    }

    public void verificaId(MouseEvent mouseEvent) {
        if(!(id.getText().equals(""))){
            bottomPane.setDisable(false);
            topPane.setDisable(true);
        }
    }

    private boolean verificaCampi() {
        if(username.getText().equals("") || password.getText().equals("") || nome.getText().equals("")
                || cognome.getText().equals("") || codFiscale.getText().equals("") || email.getText().equals("")
                || confPassword.getText().equals("")){
            return false;
        }

        return true;
    }
}
