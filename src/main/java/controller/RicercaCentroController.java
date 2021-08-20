package controller;

import client.ClientHandler;
import client.PacketReceivedListener;
import datatypes.CentroVaccinale;
import datatypes.protocolmessages.GetCVResponse;
import datatypes.protocolmessages.Packet;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
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
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Classe per controllare gli eventi e visualizzazione info nella finestra Ricerca centro vaccinale
 * @author Stefanoni Gianluca
 * @version 1.0
 */
public class RicercaCentroController implements Initializable, PacketReceivedListener {
    /**
     * Variabili per i componenti dell'interfaccia grafica
     */
    //region Variabili FXML
    @FXML
    private ChoiceBox<String> typeSearch;
    @FXML
    private ChoiceBox<String> tipologia;
    @FXML
    private TextField textFilter;
    @FXML
    private ListView<CentroVaccinale> CentriList;
    //endregion
    /**
     * Lista dei centri richiesti al server
     */
    private ObservableList<CentroVaccinale> data;
    /**
     * Centro di cui si vogliono visualizzare le informazioni
     */
    public static CentroVaccinale centoVis;
    /**
     * client è l'istanza del client connesso al server
     */
    private ClientHandler client;

    /**
     * Metodo invocato per tornare alla schermata principale dell'applicazione
     * @param mouseEvent
     */
    public void indietro(MouseEvent mouseEvent) {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../view/mainLayout.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 700, 500);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("../img/icon.png"))));
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
    /**
     * Metodo invocato durante l'inizializzazione della finestra per: settare il client, richiedere l'elenco dei centri
     * vaccinali e aggiunge un listener a ChoiceBox<String> typeSearch per controllare il cambio di stato
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = ClientHandler.getInstance();
        this.client.addListener(GetCVResponse.class.toString(), this);
        client.getAllCV();
        typeSearch.getSelectionModel()
                .selectedItemProperty()
                .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue)
                        -> choiceEnable(newValue) );
    }
    /**
     * Metodo per cambiare il tipo di ricerca (nome centro || comune + tipologia)
     * @param value
     */
    private void choiceEnable(String value){
        if(value.equals("Nome centro"))
            tipologia.setDisable(true);
        else
            tipologia.setDisable(false);
    }
    /**
     * Metodo per aggiornare i dati visualizzati nella lista, filtrati per le richieste del client, invocato da
     * Button ricerca
     * @param mouseEvent
     */
    public void ricerca(MouseEvent mouseEvent) {
        if(typeSearch.getValue().equals("Nome centro"))
            ricercaPerNome();
        else
            ricercaPerComuneTipo();
    }
    /**
     * Metodo invocato da Button deleteButton, per cancellare il contenuto della barra di ricerca
     * @param mouseEvent
     */
    public void cancella(MouseEvent mouseEvent) {
        textFilter.setText("");
    }
    /**
     * Metodo incato dal metodo ricerca se il tipo di ricerca selezionato è Nome
     */
    public void ricercaPerNome(){
        ObservableList<CentroVaccinale> dataFiltered = FXCollections.observableArrayList();
        for(CentroVaccinale centro : data) {
            if (centro.getNome().contains(textFilter.getText())){
                dataFiltered.add(centro);
            }
        }
        CentriList.setItems(dataFiltered);

        CentriList.setCellFactory(studentListView -> new CustomListCell());
        CentriList.refresh();
        System.out.println(dataFiltered.toString());
    }
    /**
     * Metodo incato dal metodo ricerca se il tipo di ricerca selezionato è Comune + Tipologia
     */
    public void ricercaPerComuneTipo(){
        ObservableList<CentroVaccinale> dataFiltered = FXCollections.observableArrayList();
        for(CentroVaccinale centro : data) {
            if (centro.getComune().contains(textFilter.getText()) && centro.getTipologia().equals(tipologia.getValue())){
                dataFiltered.add(centro);
            }
        }
        CentriList.setItems(dataFiltered);

        CentriList.setCellFactory(studentListView -> new CustomListCell());
        CentriList.refresh();
        System.out.println(dataFiltered.toString());
    }
    /**
     * Metodo per gestire la ricezione del pacchetto GetCVResponse e inserire i dati di tutti i centri nella List data
     * @param packet Pacchetto ricevuto
     */
    @Override
    public void onPacketReceived(Packet packet) {
        if(packet instanceof GetCVResponse){
            GetCVResponse res = (GetCVResponse) packet;
            List<CentroVaccinale> list = res.getCvList();
            for(CentroVaccinale cv : list){
                System.out.println(cv);
            }
            if (res.isEsito()) {
                data.addAll(list);
                CentriList.setItems(data);
                CentriList.setCellFactory(CentriListView -> new CustomListCell());
            }
        }
    }

    /**
     * Classe per creare una Custom cell per la lista di visualizzaione dei centri
     * @author Stefanoni Gianluca
     * @version 1.0
     */
    private class CustomListCell extends ListCell<CentroVaccinale> {
        /**
         * Variabili per i componenti dell'interfaccia grafica
         */
        //region Variabili FXML
        @FXML
        private Label nomeCentro;
        @FXML
        private Label tipoCentro;
        @FXML
        private Label comuneCentro;
        @FXML
        private ImageView icon;
        @FXML
        private AnchorPane anchorPane;
        //endregion

        /**
         * Metofo Principale della classe che setta all'interno della cella le info del centro, invocato ogni volta che
         * i dati vengono aggiornati
         * @param item
         * @param empty
         */
        @Override
        protected void updateItem(CentroVaccinale item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/customListCell.fxml"));
                    loader.setController(this);
                    loader.load();
                } catch (IOException exc) {
                    throw new UncheckedIOException(exc);
                }

                anchorPane.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
                            /**
                             * Metodo invocato al click di una della celle per aprire la finestra di visualizzazione del
                             * centro selzionato
                             * @param e
                             */
                            public void handle(MouseEvent e) {
                                RicercaCentroController.centoVis = item;
                                try {
                                    FXMLLoader fxmlLoader = new FXMLLoader();
                                    fxmlLoader.setLocation(getClass().getResource("../view/visualizzaCentroLayout.fxml"));
                                    Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                                    Stage stage = new Stage();
                                    stage.getIcons().add(new Image(String.valueOf(getClass().getResource("../img/icon.png"))));
                                    stage.setTitle("Info " + item.getId());
                                    stage.setScene(scene);
                                    stage.setResizable(false);
                                    stage.show();

                                    Node source = (Node) e.getSource();
                                    Stage thisStage = (Stage) source.getScene().getWindow();
                                    thisStage.close();
                                }
                                catch (IOException err) {
                                    err.printStackTrace();
                                }
                            }

                        });

                nomeCentro.setText(item.getNome());
                tipoCentro.setText(item.getTipologia());
                switch (item.getTipologia()) {
                    case "Ospedaliero":icon.setImage(new Image(String.valueOf(getClass().getResource("../img/ospedale.png")))); break;
                    case "Aziendale":icon.setImage(new Image(String.valueOf(getClass().getResource("../img/azienda.png")))); break;
                    case "Hub":icon.setImage(new Image(String.valueOf(getClass().getResource("../img/hub.png")))); break;
                }
                comuneCentro.setText(
                        item.getQualificatore() + " " +
                        item.getNomeIndirizzo() + ", " +
                        item.getNumero() + ", " +
                        item.getComune() + " (" +
                        item.getSiglaProvincia() + ")");
                setText(null);
                setGraphic(anchorPane);
            }
        }

    }
}
