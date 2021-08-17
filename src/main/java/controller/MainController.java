package controller;
import client.ClientHandler;
import client.PacketReceivedListener;
import datatypes.Vaccinato;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable, PacketReceivedListener {
    @FXML
    private Button visualizzaCentri;
    @FXML
    private Button disconnetti;
    @FXML
    private AnchorPane loginPane;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    private static ClientHandler client;
    private static Vaccinato user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = new ClientHandler();
        user = null;
        client.addListener(UserLoginResponse.class.toString(), this);
        try {
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!(Objects.isNull(user))) {
            loginPane.setVisible(false);
            disconnetti.setVisible(true);
        }
        else{
            loginPane.setVisible(true);
            disconnetti.setVisible(false);
        }
    }
    public void visualizzaInfoCentri(MouseEvent mouseEvent) {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../view/ricercaCentroLayout.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("../img/icon.png"))));
            stage.setTitle("Ricerca Centro Vaccinale");
            stage.setMinHeight(400);
            stage.setMinWidth(610);
            stage.setScene(scene);
            //stage.setResizable(false);
            stage.show();

            Node source = (Node) mouseEvent.getSource();
            Stage thisStage = (Stage) source.getScene().getWindow();
            thisStage.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(MouseEvent mouseEvent) {
            //Disconnetti
            client.disconnect();
            user = null;
            loginPane.setVisible(true);
            disconnetti.setVisible(false);
    }

    @Override
    public void onPacketReceived(Packet packet) {
        if(packet instanceof UserLoginResponse){
            UserLoginResponse res = (UserLoginResponse) packet;
            System.out.println("Login: " + res.isEsito());
            if (res.isEsito()){
                MainController.setUser(res.getVaccinato());
                //Utente loggato
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

    public static Vaccinato getUser(){
        return user;
    }

    public static void setUser(Vaccinato v){
        user = v;
    }

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
            stage.setTitle("Registrazione");
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
}
