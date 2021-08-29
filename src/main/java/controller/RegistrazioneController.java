package controller;

import client.ClientHandler;
import client.PacketReceivedListener;
import datatypes.Vaccinato;
import datatypes.Vaccinazione;
import datatypes.protocolmessages.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
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
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Classe per controllare finestra di Registrazione dell'utente
 * @author Stefanoni Gianluca
 * @version 1.0
 */
public class RegistrazioneController implements Initializable, PacketReceivedListener {
    /**
     * Variabili per i componenti dell'interfaccia grafica
     */
    //region Variabili FXML
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
    //endregion
    /**
     * Variabili per verificare la validità di Codice fiscale e Email
     */
    private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    private static final String COD_FISCALE_REGEX = "^[a-zA-Z]{6}[0-9]{2}[abcdehlmprstABCDEHLMPRST]{1}[0-9]{2}([a-zA-Z]{1}[0-9]{3})[a-zA-Z]{1}$";
    private static Pattern pattern;
    private Matcher matcher;
    /**
     * client è l'istanza del client connesso al server
     */
    private ClientHandler client;
    /**
     * Variabili per memorizzare se il controllo è andato a buon fine
     */
    private boolean verificaEmailDB = false, verificaUser = false, verficaIDVac = false;
    private String emailDBRegistrata;
    /**
     * Metodo invocato per tornare alla schermata principale dell'applicazione
     * @param mouseEvent
     */
    public void indietro(MouseEvent mouseEvent) {
        chiudi();
    }
    /**
     * Metodo che invoca la verifica dei capi e invia le informazioni al server
     * @param mouseEvent
     */
    public void controllaCittadino(MouseEvent mouseEvent) throws InterruptedException {
       if(!client.requestUserIdCheck(username.getText()))
           Platform.runLater(this::connessionePersa);

    }

    public void registraCittadino(){
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
        if(!client.requestUserRegistration(v, id.getText()))
            Platform.runLater(this::connessionePersa);
    }
    /**
     * Metodo che chiede al server di verificare se l'id della vaccinazione è registrato a DB
     * @param mouseEvent
     */
    public void verificaId(MouseEvent mouseEvent) {
        if(!client.getVaccinationByKey(id.getText()))
            Platform.runLater(this::connessionePersa);
    }
    /**
     * Metodo invocato dopo aver ricevuto la risposta dal server per la verifica dell'id della vaccinazione, in caso
     * affermativo consebte di procedere con la registrazione
     * @param res Esito della chiamata al server
     */
    private void accediRegistrazione(GetVaccinationByKeyResponse res){
        if(res.isEsito()){
            setColorBorder(id, "transparent");
            bottomPane.setDisable(false);
            topPane.setDisable(true);
            Vaccinato vaccinato = res.getVaccination().getVaccinato();
            nome.setText(vaccinato.getNome());
            cognome.setText(vaccinato.getCognome());
            codFiscale.setText(vaccinato.getCodiceFiscale());
            email.setText(vaccinato.getEmail());
            emailDBRegistrata = vaccinato.getEmail();
            nome.setEditable(false);
            cognome.setEditable(false);
            codFiscale.setEditable(false);
        }else{
            setColorBorder(id, "red");
            Alert alertLogin = new Alert(Alert.AlertType.ERROR);
            alertLogin.setTitle("");
            alertLogin.setHeaderText("ID non trovato");
            alertLogin.showAndWait();
            id.setText("");
        }
    }
    /**
     * Verifica di tutti i campi di registrazione
     * @return
     */
    private boolean verificaCampi() {
        boolean verified = true;
        if (username.getText().equals(""))
            verified = setColorBorder(username, "red");
        else {
            if (verificaUser) setColorBorder(username, "transparent");
            else setColorBorder(username, "red");
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
                    Platform.runLater(() -> {
                        Alert alertPassword = new Alert(Alert.AlertType.ERROR);
                        alertPassword.setTitle("");
                        alertPassword.setHeaderText("Errore nella compilazione dei campi");
                        alertPassword.setContentText("Le password non corrispondono");

                        alertPassword.showAndWait();
                    });
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
                Platform.runLater(() -> {
                    Alert alertCod = new Alert(Alert.AlertType.ERROR);
                    alertCod.setTitle("");
                    alertCod.setHeaderText("Errore nella compilazione dei campi");
                    alertCod.setContentText("Codice fiscale non valido");

                    alertCod.showAndWait();
                });
            }
        }

        if (email.getText().equals(""))
            verified = setColorBorder(email, "red");
        else {
            if (verificaEmail(email.getText()) && verificaEmailDB)
                setColorBorder(email, "transparent");
            else {
                verified = setColorBorder(email, "red");
                Platform.runLater(() -> {
                    Alert alertEmail = new Alert(Alert.AlertType.ERROR);
                    alertEmail.setTitle("");
                    alertEmail.setHeaderText("Errore nella compilazione dei campi");
                    alertEmail.setContentText("Email non valida");

                    alertEmail.showAndWait();
                });
            }
        }


        return verified;
    }
    /**
     * Verifica con regex dell'email
     * @param email Email da verificare
     * @return
     */
    private boolean verificaEmail(String email) {
        pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    /**
     * Verifica con regex del codice fiscale
     * @param codFiscale Codice fiscale da verificare
     * @return
     */
    private boolean verificaCodFiscale(String codFiscale) {
        pattern = Pattern.compile(COD_FISCALE_REGEX, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(codFiscale);
        return matcher.matches();
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
    /**
     * Metodo invocato dopo aver riceuto la risposta dal server riguardo la registrazione dell'utente, viene comunicato
     * l'esito anche all'utente
     * @param res
     */
    private void risultatoRegistrazione(UserRegistrationResponse res){

        if (res.isEsito()) {
            //Alert con id da dare al cittadino
            Platform.runLater(() -> {
                Alert alert;
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informazioni cittadino: " + codFiscale.getText());
                alert.setHeaderText(null);
                alert.setContentText("ID vaccinazione: " + id.getText() + ". Registrazione completata");
                alert.showAndWait();

                chiudi();
            });
        }else{
            Platform.runLater(() -> {
                Alert alert;
                //Alert con id da dare al cittadino
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Informazioni cittadino: " + codFiscale.getText());
                alert.setHeaderText(null);
                alert.setContentText("Registrazione fallita, riprovare");
                alert.showAndWait();
            });
        }
    }
    /**
     * Metodo invocato per chiudere la finestra e aprire il Menu principale
     */
    private void chiudi() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../view/mainLayout.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 700, 500);
            Stage stage = new Stage();
            stage.setTitle("Vaccinazioni Cittadini");
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("../img/icon.png"))));
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    Platform.exit();
                    System.exit(0);
                }
            });
            stage.show();

            Stage thisStage = (Stage) username.getScene().getWindow();
            thisStage.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Metodo per gestire la ricezione dei pacchetti: UserRegistrationResponse, GetVaccinationByKeyResponse,
     * CheckUserIdResponse e CheckEmailResponse
     * @param packet Pacchetto ricevuto
     */
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
            verificaUser = !((CheckUserIdResponse)packet).isEsito();
            if(!email.getText().equals("") && !email.getText().equals(emailDBRegistrata))
                if(!client.requestEmailCheck(email.getText()))
                    Platform.runLater(this::connessionePersa);
            else {
                verificaEmailDB = true;
                registraCittadino();
            }
        }
        if(packet instanceof CheckEmailResponse){
            System.out.println("Esiste email? " + ((CheckEmailResponse)packet).isEsito());
            verificaEmailDB = !((CheckEmailResponse)packet).isEsito();
            registraCittadino();
        }

    }

    private void connessionePersa() {
        chiudi();
    }

    /**
     * Metodo invocato durante l'inizializzazione della finestra per: settare il client
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = ClientHandler.getInstance();
        this.client.addListener(UserRegistrationResponse.class.toString(), this);
        this.client.addListener(CheckUserIdResponse.class.toString(), this);
        this.client.addListener(CheckEmailResponse.class.toString(), this);
        this.client.addListener(GetVaccinationByKeyResponse.class.toString(), this);
    }
}
