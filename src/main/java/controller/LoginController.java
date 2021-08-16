package controller;

import client.ClientHandler;
import client.PacketReceivedListener;
import datatypes.protocolmessages.Packet;
import datatypes.protocolmessages.UserLoginResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.swing.event.CaretListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable, PacketReceivedListener {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label labelIndietro;

    private ClientHandler client;



    public void loginRequest(MouseEvent mouseEvent) {
        if(!verificaCampi()) return;

        client.requestUserLogin(username.getText(), password.getText());

    }

    public void registrazione(MouseEvent mouseEvent) {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../view/registrazioneLayout.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 450);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("../img/icon.png"))));
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
       chiudi();
    }
    private boolean verificaCampi() {
        boolean verified = true;
        if(username.getText().equals(""))
            verified = setColorBorder(username, "red");
        else
            setColorBorder(username, "transparent");

        if(password.getText().equals(""))
            verified = setColorBorder(password, "red");
        else
            setColorBorder(password, "transparent");

        return verified;
    }

    private boolean setColorBorder(Control component, String color){
        component.setStyle("-fx-border-color: " + color + ";");
        return false;
    }

    @Override
    public void onPacketReceived(Packet packet) {
        if(packet instanceof UserLoginResponse){
            UserLoginResponse res = (UserLoginResponse) packet;
            System.out.println("Login: " + res.isEsito());
            if (res.isEsito()){
                MainController.setUser(res.getVaccinato());
                chiudi();
            }else{
                Alert alertLogin = new Alert(Alert.AlertType.ERROR);
                alertLogin.setTitle("");
                alertLogin.setHeaderText("User e/o password errati");
                alertLogin.showAndWait();
                username.setText("");
                password.setText("");
            }
        }
    }

    private void chiudi() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../view/mainLayout.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 300);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("../img/icon.png"))));
            stage.setTitle("Vaccinazioni Cittadini");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            Stage thisStage = (Stage) username.getScene().getWindow();
            thisStage.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = ClientHandler.getInstance();
        this.client.addListener(UserLoginResponse.class.toString(), this);
    }
}
