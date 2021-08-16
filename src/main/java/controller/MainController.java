package controller;
import client.ClientHandler;
import client.PacketReceivedListener;
import datatypes.Vaccinato;
import datatypes.protocolmessages.Packet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable, PacketReceivedListener {
    @FXML
    private Button visualizzaCentri;
    @FXML
    private Button login;

    private static ClientHandler client;
    private static Vaccinato user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = new ClientHandler();
        user = null;
        try {
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!(Objects.isNull(user)))
            login.setText("Disconnetti");
        else
            login.setText("Login");
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

    public void loginDisconnect(MouseEvent mouseEvent) {
        if (Objects.isNull(user)){
            //Login
            Parent root;
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("../view/loginLayout.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 500, 300);
                Stage stage = new Stage();
                stage.getIcons().add(new Image(String.valueOf(getClass().getResource("../img/icon.png"))));
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
            client.disconnect();
            user = null;
            login.setText("Login");
        }
    }


    @Override
    public void onPacketReceived(Packet packet) {

    }

    public static Vaccinato getUser(){
        return user;
    }

    public static void setUser(Vaccinato v){
        user = v;
    }
}
