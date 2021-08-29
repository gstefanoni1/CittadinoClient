package controller;

import client.ClientHandler;
import client.PacketReceivedListener;
import datatypes.CentroVaccinale;
import datatypes.EventoAvverso;
import datatypes.TipologiaEventoAvverso;
import datatypes.protocolmessages.GetEvTypologiesResponse;
import datatypes.protocolmessages.Packet;
import datatypes.protocolmessages.RegistrationEVResponse;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
/**
 * Classe per controllare finestra di Inserimento evento avverso
 * @author Stefanoni Gianluca
 * @version 1.0
 */
public class InserimentoEAController implements Initializable, PacketReceivedListener {
    /**
     * Variabili per i componenti dell'interfaccia grafica
     */
    //region Variabili FX
    @FXML
    private CheckBox checkMalDiTesta;
    @FXML
    private CheckBox checkFebbre;
    @FXML
    private CheckBox checkDMA;
    @FXML
    private CheckBox checkLinfo;
    @FXML
    private CheckBox checkTachicardia;
    @FXML
    private CheckBox checkCrisiIper;
    @FXML
    private ChoiceBox<String> severitaMalDiTesta;
    @FXML
    private ChoiceBox<String> severitaFebbre;
    @FXML
    private ChoiceBox<String> severitaDMA;
    @FXML
    private ChoiceBox<String> severitaLinfo;
    @FXML
    private ChoiceBox<String> severitaTachicardia;
    @FXML
    private ChoiceBox<String> severitaCrisiIper;
    @FXML
    private TextArea noteMalDiTesta;
    @FXML
    private TextArea noteFebbre;
    @FXML
    private TextArea noteDMA;
    @FXML
    private TextArea noteLinfo;
    @FXML
    private TextArea noteTachicardia;
    @FXML
    private TextArea noteCrisiIper;
    //endregion
    /**
     * client è l'istanza del client connesso al server
     */
    private ClientHandler client;
    /**
     * centro è il centro di riferimento a cui si vuole aggiungere gli eventi avversi
     */
    private CentroVaccinale centro;
    /**
     * tipologie di eventi
     */
    private ListProperty<TipologiaEventoAvverso> tipoeventi;

    /**
     * Contatore delle richieste inviate
     */
    private int contEVSelezionati = 0;
    /**
     * Contatore eventi avversi registrati
     */
    private int contEVRegistrati = 0;

    /**
     * Metodo invocato per tornare alla schermata di visualizazione delle info
     * @param mouseEvent
     */
    public void indietro(MouseEvent mouseEvent) {
        chiudi();
    }

    /**
     * Metodo che controlla i campi e invia le informazioni al server
     * @param mouseEvent
     */
    public void inserisciEA(MouseEvent mouseEvent) {
        int i = 1;
        if (checkMalDiTesta.isSelected()){
            contEVSelezionati++;
           setEvento(tipoeventi.get(0).getNome(), Integer.parseInt(severitaMalDiTesta.getValue()),
                   noteMalDiTesta.getText(), i);
        }
        i++;
        if (checkFebbre.isSelected()){
            contEVSelezionati++;
            setEvento(tipoeventi.get(1).getNome(), Integer.parseInt(severitaFebbre.getValue()),
                    noteFebbre.getText(), i);
        }
        i++;
        if (checkDMA.isSelected()){
            contEVSelezionati++;
            setEvento(tipoeventi.get(2).getNome(), Integer.parseInt(severitaDMA.getValue()),
                    noteDMA.getText(), i);
        }
        i++;
        if (checkLinfo.isSelected()){
            contEVSelezionati++;
            setEvento(tipoeventi.get(3).getNome(), Integer.parseInt(severitaLinfo.getValue()),
                    noteLinfo.getText(), i);
        }
        i++;
        if (checkTachicardia.isSelected()){
            contEVSelezionati++;
            setEvento(tipoeventi.get(4).getNome(), Integer.parseInt(severitaTachicardia.getValue()),
                    noteTachicardia.getText(), i);
        }
        i++;
        if (checkCrisiIper.isSelected()){
            contEVSelezionati++;
            setEvento(tipoeventi.get(5).getNome(), Integer.parseInt(severitaCrisiIper.getValue()),
                    noteCrisiIper.getText(), i);
        }
       if(contEVSelezionati == 0)
       {
           Alert alert;
           alert = new Alert(Alert.AlertType.ERROR);
           alert.setTitle("Errore registrazione");
           alert.setHeaderText(null);
           alert.setContentText("Selezionare almeno una tipologia");
           alert.showAndWait();
       }
    }

    /**
     * Crea il singolo evento avverso e lo manda a server
     * @param tipo Tipologia di evento
     * @param severita Severità dell'evento
     * @param note Note opzionali aggiuntive riguardo l'evento
     */
    private void setEvento(String tipo, int severita, String note, int id){
        EventoAvverso evento = new EventoAvverso();
        evento.setCentroVaccinale(centro);
        TipologiaEventoAvverso tipologia = new TipologiaEventoAvverso(tipo);
        tipologia.setId(id);
        evento.setTipologia(tipologia);
        evento.setSeverita(severita);
        evento.setNote(note);
        if(!client.insertEV(evento))
            Platform.runLater(this::connessionePersa);
    }

    private void connessionePersa() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../view/mainLayout.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 700, 500);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("../img/icon.png"))));
            stage.setTitle("Vaccinazioni Cittadini");
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

            Stage thisStage = (Stage) checkCrisiIper.getScene().getWindow();
            thisStage.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //region Metodi per abilitazione e disabilitazione dei campi di sevreirà e note opzionali dei singoli eventi avversi
    /**
     * Metodi per gestire l'abilitazione e la disabilitazione dei campi di severita e note opzionali della tipologia
     * Mal di testa
     * @param mouseEvent
     */
    public void checkedMalDiTesta(MouseEvent mouseEvent) {
        if(checkMalDiTesta.isSelected()){
            severitaMalDiTesta.setDisable(false);
            noteMalDiTesta.setDisable(false);
        }else{
            severitaMalDiTesta.setDisable(true);
            noteMalDiTesta.setDisable(true);
            noteMalDiTesta.setText("");
        }
    }
    /**
     * Metodi per gestire l'abilitazione e la disabilitazione dei campi di severita e note opzionali della tipologia
     * Mal di testa
     * @param mouseEvent
     */
    public void checkedFebbre(MouseEvent mouseEvent) {
        if(checkFebbre.isSelected()){
            severitaFebbre.setDisable(false);
            noteFebbre.setDisable(false);
        }else{
            severitaFebbre.setDisable(true);
            noteFebbre.setDisable(true);
            noteFebbre.setText("");
        }
    }
    /**
     * Metodi per gestire l'abilitazione e la disabilitazione dei campi di severita e note opzionali della tipologia
     * Febbre
     * @param mouseEvent
     */
    public void checkedDMA(MouseEvent mouseEvent) {
        if(checkDMA.isSelected()){
            severitaDMA.setDisable(false);
            noteDMA.setDisable(false);
        }else{
            severitaDMA.setDisable(true);
            noteDMA.setDisable(true);
            noteDMA.setText("");
        }
    }
    /**
     * Metodi per gestire l'abilitazione e la disabilitazione dei campi di severita e note opzionali della tipologia
     * Dolori muscolari e articolari
     * @param mouseEvent
     */
    public void checkedLinfo(MouseEvent mouseEvent) {
        if(checkLinfo.isSelected()){
            severitaLinfo.setDisable(false);
            noteLinfo.setDisable(false);
        }else{
            severitaLinfo.setDisable(true);
            noteLinfo.setDisable(true);
            noteLinfo.setText("");
        }
    }
    /**
     * Metodi per gestire l'abilitazione e la disabilitazione dei campi di severita e note opzionali della tipologia
     * Linfoadenopatia
     * @param mouseEvent
     */
    public void checkedTachicardia(MouseEvent mouseEvent) {
        if(checkTachicardia.isSelected()){
            severitaTachicardia.setDisable(false);
            noteTachicardia.setDisable(false);
        }else{
            severitaTachicardia.setDisable(true);
            noteTachicardia.setDisable(true);
            noteTachicardia.setText("");
        }
    }
    /**
     * Metodi per gestire l'abilitazione e la disabilitazione dei campi di severita e note opzionali della tipologia
     * Tachicardia
     * @param mouseEvent
     */
    public void checkedCrisiIper(MouseEvent mouseEvent) {
        if(checkCrisiIper.isSelected()){
            severitaCrisiIper.setDisable(false);
            noteCrisiIper.setDisable(false);
        }else{
            severitaCrisiIper.setDisable(true);
            noteCrisiIper.setDisable(true);
            noteCrisiIper.setText("");
        }
    }
    //endregion
    /**
     * Metodo per gestire la ricezione del pacchetto RegistrationEVResponse
     * @param packet Pacchetto ricevuto
     */
    @Override
    public void onPacketReceived(Packet packet) {
        if (packet instanceof RegistrationEVResponse){
            RegistrationEVResponse res = (RegistrationEVResponse) packet;
            contEVRegistrati++;
            if (res.isEsito()) {
                if(contEVSelezionati == contEVRegistrati) {
                    Platform.runLater(() -> {
                        Alert alert;
                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Informazioni registrate");
                        alert.setHeaderText(null);
                        alert.setContentText("Registrazione completata");
                        alert.showAndWait();
                        chiudi();
                    });

                }
            }else{
                Platform.runLater(() -> {
                    Alert alert;
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Errore registrazione");
                    alert.setHeaderText(null);
                    alert.setContentText("Registrazione fallita, riprovare");
                    alert.showAndWait();
                });
            }
        }

        if(packet instanceof GetEvTypologiesResponse){
            GetEvTypologiesResponse res = (GetEvTypologiesResponse) packet;
            if (res.isEsito()){
               tipoeventi.addAll(res.getTypologies());
            }else{
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Errore");
                    alert.setHeaderText(null);
                    alert.setContentText("Impossibile recuperare tiopologia Eventi");
                    alert.showAndWait();
                    chiudi();
                });
            }
        }
    }
    /**
     * Metodo invocato per tornare alla schermata di visualizazione delle info
     */
    private void chiudi() {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../view/visualizzaCentroLayout.fxml"));

            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("../img/icon.png"))));
            stage.setTitle("Info " + centro.getId());
            stage.setScene(scene);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    Platform.exit();
                    System.exit(0);
                }
            });
            stage.show();

            Stage thisStage = (Stage) checkMalDiTesta.getScene().getWindow();
            thisStage.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * Metodo invocato durante l'inizializzazione della finestra per: settare il client
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tipoeventi = new SimpleListProperty<>(FXCollections.observableArrayList());
        centro = RicercaCentroController.centroVis;
        client = ClientHandler.getInstance();
        this.client.addListener(GetEvTypologiesResponse.class.toString(), this);
        this.client.addListener(RegistrationEVResponse.class.toString(), this);
        if(!client.getEvTypologies())
            Platform.runLater(this::connessionePersa);
    }
}
