package controller;

import client.ClientHandler;
import client.PacketReceivedListener;
import datatypes.CentroVaccinale;
import datatypes.EventoAvverso;
import datatypes.ReportCV;
import datatypes.TipologiaEventoAvverso;
import datatypes.protocolmessages.GetCVResponse;
import datatypes.protocolmessages.GetReportResponse;
import datatypes.protocolmessages.Packet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.swing.event.CaretListener;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;

/**
 * Classe per controllare gli eventi e visualizzazione info nella finestra Visualizza centro vaccinale
 * @author Stefanoni Gianluca
 * @version 1.0
 */
public class VisualizzaCentroController implements Initializable, PacketReceivedListener {
    /**
     * Variabili per i componenti dell'interfaccia grafica
     */
    //region Variabili FXML
    @FXML
    private Label nomeCentro;
    @FXML
    private Label indirizzo;
    @FXML
    private Label tipologia;
    @FXML
    private ImageView icon;
    @FXML
    private BarChart barChart;
    @FXML
    private Button eventoAvverso;
    //endregion
    /**
     * client è l'istanza del client connesso al server
     */
    private ClientHandler client;

    /**
     * Variabili per la creaione delle colonne del grafico
     */
    private XYChart.Series series5 = new XYChart.Series();
    private XYChart.Series series4 = new XYChart.Series();
    private XYChart.Series series3 = new XYChart.Series();
    private XYChart.Series series2 = new XYChart.Series();
    private XYChart.Series series1 = new XYChart.Series();

    /**
     * Metodo invocato per tornare alla schermata di ricerca centri
     * @param mouseEvent
     */
    public void indietro(MouseEvent mouseEvent) {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../view/ricercaCentroLayout.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("../img/icon.png"))));
            stage.setTitle("Visualizza Centro Vaccinale");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            RicercaCentroController.centoVis = new CentroVaccinale();

            Node source = (Node) mouseEvent.getSource();
            Stage thisStage = (Stage) source.getScene().getWindow();
            thisStage.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Metodo invocato durante l'inizializzazione della finestra, per: settare il client, visualizzare le info del
     * centro selzionato e richiedere dati per centro per comporre il grafico barchart con gli eventi avversi
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        visualizzaBottoneEventiAvversi();

        client = ClientHandler.getInstance();
        this.client.addListener(GetCVResponse.class.toString(), this);
        client.getReport(RicercaCentroController.centoVis);

        nomeCentro.setText(RicercaCentroController.centoVis.getNome());
        tipologia.setText(RicercaCentroController.centoVis.getTipologia());

        //Ricostruisco indirizzo
        indirizzo.setText(
                RicercaCentroController.centoVis.getQualificatore() + " " +
                RicercaCentroController.centoVis.getNomeIndirizzo() + ", " +
                RicercaCentroController.centoVis.getNumero() + ", " +
                RicercaCentroController.centoVis.getComune() + " (" +
                RicercaCentroController.centoVis.getSiglaProvincia() + "), " +
                RicercaCentroController.centoVis.getCap());

        switch (RicercaCentroController.centoVis.getTipologia()) {
            case "Ospedaliero": icon.setImage(new Image(String.valueOf(getClass().getResource("../img/ospedale.png")))); break;
            case "Aziendale": icon.setImage(new Image(String.valueOf(getClass().getResource("../img/azienda.png")))); break;
            case "Hub": icon.setImage(new Image(String.valueOf(getClass().getResource("../img/hub.png")))); break;
        }

        series5.setName("5");
        series4.setName("4");
        series3.setName("3");
        series2.setName("2");
        series1.setName("1");


    }

    /**
     * Metodo invocato dal initialize, per mostrare il bottone di inserimento evento avverso
     */
    private void visualizzaBottoneEventiAvversi() {
        //TODO getCentro da username oppure inserire variabile in vaccinato
        if(Objects.isNull(MainController.getUser()) ){
            eventoAvverso.setVisible(true);
        }
    }

    /**
     * Metodo per inserire i dati nel grafico
     * @param eventiAvversi Lista contenente i dati ricevuti dal server
     */
    private void popolaGrafico(List<String> eventiAvversi) {
        for(int i = 0; i < eventiAvversi.size();){
            inserisciColonna(eventiAvversi.get(i++), Integer.parseInt(eventiAvversi.get(i++)),
                    Math.round(Float.parseFloat(eventiAvversi.get(i++))));
        }

        barChart.getData().addAll(series5, series4, series3, series2, series1);
        System.out.println(series1.getData().toString());
        System.out.println(series2.getData().toString());
        System.out.println(series3.getData().toString());
        System.out.println(series4.getData().toString());
        System.out.println(series5.getData().toString());
    }
    /**
     * Metodo per l'inserimento delle singole colonne in base alla severità del singolo evento
     * @param nome Nome da dare alla colonna
     * @param cont Numero di eventi segnalati per quella tipologia
     * @param severita Severità media arrotondata
     */
    private void inserisciColonna(String nome, int cont, int severita){

        switch (severita){
            case 1:series1.getData().add(new XYChart.Data(nome, cont)); break;
            case 2:series2.getData().add(new XYChart.Data(nome, cont)); break;
            case 3:series3.getData().add(new XYChart.Data(nome, cont)); break;
            case 4:series4.getData().add(new XYChart.Data(nome, cont)); break;
            case 5:series5.getData().add(new XYChart.Data(nome, cont)); break;
        }
    }

    /**
     * Metodo invocato dal bottone @eventoAvverso per aprire la finestra di inserimento
     * @param mouseEvent
     */
    public void inserisciEA(MouseEvent mouseEvent) {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../view/inserimentoEALayout.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 300);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("../img/icon.png"))));
            stage.setTitle("Inserimento evento avverso");
            stage.setMinHeight(300);
            stage.setMinWidth(500);
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
     * Metodo per gestire la ricezione del pacchetto GetReportResponse e inserire i dati del report
     * @param packet Pacchetto ricevuto
     */
    @Override
    public void onPacketReceived(Packet packet) {
        if(packet instanceof GetReportResponse){
            ReportCV report = ((GetReportResponse)packet).getReport();
            System.out.println(report.getReportList());
            popolaGrafico(report.getReportList());
        }
    }
}
