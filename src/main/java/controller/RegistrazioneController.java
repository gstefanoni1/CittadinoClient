package controller;

import client.ClientHandler;
import client.PacketReceivedListener;
import datatypes.Vaccinato;
import datatypes.Vaccinazione;
import datatypes.protocolmessages.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrazioneController implements Initializable, PacketReceivedListener {

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

    private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    private static final String COD_FISCALE_REGEX = "^[a-zA-Z]{6}[0-9]{2}[abcdehlmprstABCDEHLMPRST]{1}[0-9]{2}([a-zA-Z]{1}[0-9]{3})[a-zA-Z]{1}$";
    private static Pattern pattern;
    private Matcher matcher;
    private ClientHandler client;
    private boolean verificaEmailDB = false, verificaUser = false, verficaIDVac = false;


    public void indietro(MouseEvent mouseEvent) {
        chiudi();
    }

    public void registraCittadino(MouseEvent mouseEvent) {
        client.requestUserIdCheck(username.getText());
        client.requestEmailCheck(email.getText());
        if(!verificaCampi()){
            return;
        }
        Vaccinato v = new Vaccinato();
        v.setCognome(cognome.getText());
        v.setNome(nome.getText());
        v.setCodiceFiscale(codFiscale.getText());
        v.setEmail(email.getText());
        v.setPassword(password.getText());
        v.setUserId(username.getText());
        client.requestUserRegistration(v, id.getText());
    }

    public void verificaId(MouseEvent mouseEvent) {
        client.getVaccinationByKey(id.getText());
    }

    private void accediRegistrazione(GetVaccinationByKeyResponse res){
        if(res.isEsito()){
            setColorBorder(id, "transparent");
            bottomPane.setDisable(false);
            topPane.setDisable(true);
        }else{
            setColorBorder(id, "red");
            Alert alertLogin = new Alert(Alert.AlertType.ERROR);
            alertLogin.setTitle("");
            alertLogin.setHeaderText("ID non trovato");
            alertLogin.showAndWait();
            id.setText("");
        }
    }

    private boolean verificaCampi() {
        boolean verified = true;
        if (username.getText().equals(""))
            verified = setColorBorder(username, "red");
        else {
            if (verificaUser) setColorBorder(username, "transparent");
        }


        if (password.getText().equals(""))
            verified = setColorBorder(password, "red");
        else {
            setColorBorder(password, "transparent");
            if(confPassword.getText().equals(""))
                verified = setColorBorder(confPassword, "red");
            else {
                if (confPassword.getText().equals(password.getText())){
                    setColorBorder(confPassword, "transparent");
                }else {
                    verified = setColorBorder(confPassword, "red");
                    Alert alertPassword = new Alert(Alert.AlertType.ERROR);
                    alertPassword.setTitle("");
                    alertPassword.setHeaderText("Errore nella compilazione dei campi");
                    alertPassword.setContentText("Le password non corrispondono");

                    alertPassword.showAndWait();
                }
            }
        }

        if (nome.getText().equals(""))
            verified = setColorBorder(nome, "red");
        else setColorBorder(nome, "transparent");

        if (cognome.getText().equals(""))
            verified = setColorBorder(cognome, "red");
        else setColorBorder(cognome, "transparent");

        if (codFiscale.getText().equals(""))
            verified = setColorBorder(codFiscale, "red");
        else {
            if(verificaCodFiscale(codFiscale.getText()))
                setColorBorder(codFiscale, "transparent");
            else{
                verified = setColorBorder(codFiscale, "red");
                Alert alertCod = new Alert(Alert.AlertType.ERROR);
                alertCod.setTitle("");
                alertCod.setHeaderText("Errore nella compilazione dei campi");
                alertCod.setContentText("Codice fiscale non valido");

                alertCod.showAndWait();
            }
        }

        if (email.getText().equals(""))
            verified = setColorBorder(email, "red");
        else {
            if (verificaEmail(email.getText()) && verificaEmailDB)
                setColorBorder(email, "transparent");
            else {
                verified = setColorBorder(email, "red");
                Alert alertEmail = new Alert(Alert.AlertType.ERROR);
                alertEmail.setTitle("");
                alertEmail.setHeaderText("Errore nella compilazione dei campi");
                alertEmail.setContentText("Email non valida");

                alertEmail.showAndWait();
            }
        }


        return verified;
    }


    private boolean verificaEmail(String email) {
        pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean verificaCodFiscale(String codFiscale) {
        pattern = Pattern.compile(COD_FISCALE_REGEX, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(codFiscale);
        return matcher.matches();
    }

    private boolean setColorBorder(Control component, String color){
        component.setStyle("-fx-border-color: " + color + ";");
        return false;
    }

    private void risultatoRegistrazione(UserRegistrationResponse res){
        Alert alert;
        if (res.isEsito()) {
            //Alert con id da dare al cittadino
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informazioni cittadino: " + codFiscale.getText());
            alert.setHeaderText(null);
            alert.setContentText("ID vaccinazione: " + id.getText() + ". Registrazione completata");
            alert.showAndWait();

            chiudi();
        }else{
            //Alert con id da dare al cittadino
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Informazioni cittadino: " + codFiscale.getText());
            alert.setHeaderText(null);
            alert.setContentText("Registrazione fallita, riprovare");
            alert.showAndWait();
        }
    }

    private void chiudi() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../view/loginLayout.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 300);
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("../img/icon.png"))));
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
    public void onPacketReceived(Packet packet) {
        if(packet instanceof UserRegistrationResponse){
            UserRegistrationResponse res = (UserRegistrationResponse) packet;
            System.out.println("Registrazione: " + res.isEsito());
            risultatoRegistrazione(res);

        }
        if(packet instanceof GetVaccinationByKeyResponse){
            GetVaccinationByKeyResponse res = (GetVaccinationByKeyResponse) packet;
            accediRegistrazione(res);
        }
        if(packet instanceof CheckUserIdResponse){
            System.out.println("Esiste userId? " + ((CheckUserIdResponse)packet).isEsito());
            verificaUser = ((CheckUserIdResponse)packet).isEsito();
        }
        if(packet instanceof CheckEmailResponse){
            System.out.println("Esiste email? " + ((CheckEmailResponse)packet).isEsito());
            verificaEmailDB = ((CheckEmailResponse)packet).isEsito();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = ClientHandler.getInstance();
        this.client.addListener(UserRegistrationResponse.class.toString(), this);
        this.client.addListener(CheckUserIdResponse.class.toString(), this);
        this.client.addListener(CheckEmailResponse.class.toString(), this);
        this.client.addListener(GetVaccinationByKeyResponse.class.toString(), this);
    }
}
