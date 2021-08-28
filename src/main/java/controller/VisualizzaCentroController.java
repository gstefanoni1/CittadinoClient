package controller;

import client.ClientHandler;
import client.PacketReceivedListener;
import datatypes.CentroVaccinale;
import datatypes.ReportCV;
import datatypes.protocolmessages.CheckVaccinatedCVResponse;
import datatypes.protocolmessages.GetCVResponse;
import datatypes.protocolmessages.GetReportResponse;
import datatypes.protocolmessages.Packet;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
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
    private BarChart<String, Number> barChart;
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
    private final XYChart.Series<String, Number> series5 = new XYChart.Series<>();
    private final XYChart.Series<String, Number> series4 = new XYChart.Series<>();
    private final XYChart.Series<String, Number> series3 = new XYChart.Series<>();
    private final XYChart.Series<String, Number> series2 = new XYChart.Series<>();
    private final XYChart.Series<String, Number> series1 = new XYChart.Series<>();

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
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    Platform.exit();
                    System.exit(0);
                }
            });
            stage.show();

            RicercaCentroController.centroVis = new CentroVaccinale();

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
        client = ClientHandler.getInstance();
        this.client.addListener(GetCVResponse.class.toString(), this);
        this.client.addListener(GetReportResponse.class.toString(), this);
        this.client.addListener(CheckVaccinatedCVResponse.class.toString(), this);
        client.getReport(RicercaCentroController.centroVis);
        client.requestVaccinatedCvCheck(RicercaCentroController.centroVis);
        nomeCentro.setText(RicercaCentroController.centroVis.getNome());
        tipologia.setText(RicercaCentroController.centroVis.getTipologia());
        eventoAvverso.setVisible(false);
        //Ricostruisco indirizzo
        indirizzo.setText(
                RicercaCentroController.centroVis.getQualificatore() + " " +
                RicercaCentroController.centroVis.getNomeIndirizzo() + ", " +
                RicercaCentroController.centroVis.getNumero() + ", " +
                RicercaCentroController.centroVis.getComune() + " (" +
                RicercaCentroController.centroVis.getSiglaProvincia() + "), " +
                RicercaCentroController.centroVis.getCap());

        switch (RicercaCentroController.centroVis.getTipologia()) {
            case "Ospedaliero": icon.setImage(new Image(String.valueOf(getClass().getResource("../img/ospedale.png")))); break;
            case "Aziendale": icon.setImage(new Image(String.valueOf(getClass().getResource("../img/azienda.png")))); break;
            case "HUB": icon.setImage(new Image(String.valueOf(getClass().getResource("../img/hub.png")))); break;
        }

        series5.setName("5");
        series4.setName("4");
        series3.setName("3");
        series2.setName("2");
        series1.setName("1");
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
        Platform.runLater(() -> {
                    barChart.getData().addAll(series5, series4, series3, series2, series1);
        });
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
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("../view/visualizzaCentroLayout.fxml"));
                        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                        Stage stage = new Stage();
                        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("../img/icon.png"))));
                        stage.setTitle("Info " + nomeCentro.getText());
                        stage.setScene(scene);
                        stage.setOnCloseRequest(event1 -> {
                            Platform.exit();
                            System.exit(0);
                        });
                        stage.show();
                    }
                    catch(Exception ignored){}
                }
            });
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
            //ARANCIO SCURO #f0602c
            //ARANCIO #f8a31a
            //VERDE #56b656
            //AZZURRO CHIARO #40a7c7
            //BLU #4157c7

        }

        if (packet instanceof CheckVaccinatedCVResponse){
            CheckVaccinatedCVResponse res = (CheckVaccinatedCVResponse) packet;
            eventoAvverso.setVisible(res.isEsito());
        }

    }
}
