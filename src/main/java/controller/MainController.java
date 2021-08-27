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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Classe per controllare finestra Menu principale con login
 * @author Stefanoni Gianluca
 * @version 1.0
 */
public class MainController implements Initializable, PacketReceivedListener {
    /**
     * Variabili per i componenti dell'interfaccia grafica
     */
    //region Variabili FX
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
    @FXML
    private AnchorPane serverError;
    @FXML
    private GridPane homePane;
    @FXML
    private ImageView ricercaImg;
    //endregion
    /**
     * client è l'istanza del client connesso al server
     */
    private static ClientHandler client;
    /**
     * Variabile utilizzata per riconoscere l'utente dopo la login
     */
    private static Vaccinato user = null;
    /**
     * Metodo invocato durante l'inizializzazione della finestra per: settare il client e gestire la visualizzazione del
     * form di login
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = ClientHandler.getInstance();
        if(!client.isConnected()){
            try {
                if (client.connect()) {
                    serverError.setVisible(false);
                    client.addListener(UserLoginResponse.class.toString(), this);
                } else {
                    visualizzaPannelloRiconnessione();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    private void visualizzaPannelloRiconnessione() {
        serverError.setVisible(true);
        ricercaImg.setOpacity(0.5);
        homePane.setDisable(true);
    }

    /**
     * Metodo per ritentare la connesione dopo che è fallita
     * @param mouseEvent
     */
    public void retryConnect(MouseEvent mouseEvent) {
        serverError.setDisable(true);
        try {
            if (client.connect()) {
                serverError.setVisible(false);
                ricercaImg.setOpacity(1);
                homePane.setDisable(false);
                client.addListener(UserLoginResponse.class.toString(), this);
            } else {
                serverError.setVisible(true);
                ricercaImg.setOpacity(0.5);
                serverError.setDisable(false);
                homePane.setDisable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo che apre la finestra di ricerca dei centri vaccinali
     * @param mouseEvent
     */
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

    /**
     * Metodo che disconnette l'utente corrente
     * @param mouseEvent
     */
    public void disconnect(MouseEvent mouseEvent) {
            //Disconnetti
            user = null;
            loginPane.setVisible(true);
            disconnetti.setVisible(false);
    }
    /**
     * Metodo per gestire la ricezione del pacchetto UserLoginResponse
     * @param packet Pacchetto ricevuto
     */
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

    /**
     * Metodo per chiedere al server se c'è la corrispondenza di username e password, in caso affermaivo l'utente viene
     * loggato sul client
     * @param mouseEvent
     */
    public void loginRequest(MouseEvent mouseEvent) {
        if(!verificaCampi()) return;

        if(!client.requestUserLogin(username.getText(), password.getText()))
            visualizzaPannelloRiconnessione();

    }

    /**
     * Metodo per aprire la finstra di registrazione
     * @param mouseEvent
     */
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

    /**
     * Metodo che verifica se i campi di form di login sono compilati correttamente
     * @return
     */
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
    /**
     * Metodo per impostare il colore del bordo nei componenti grafici
     * @param component Componente da modificare
     * @param color Colore del bordo da impostare
     * @return
     */
    private boolean setColorBorder(Control component, String color){
        component.setStyle("-fx-border-color: " + color + ";");
        return false;
    }
}
