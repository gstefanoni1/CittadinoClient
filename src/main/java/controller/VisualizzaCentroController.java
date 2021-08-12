package controller;

import datatypes.CentroVaccinale;
import datatypes.EventoAvverso;
import datatypes.TipologiaEventoAvverso;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class VisualizzaCentroController implements Initializable {
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

    //Creo le diverse colonne
    private XYChart.Series series0 = new XYChart.Series();
    private XYChart.Series series1 = new XYChart.Series();
    private XYChart.Series series2 = new XYChart.Series();
    private XYChart.Series series3 = new XYChart.Series();
    private XYChart.Series series4 = new XYChart.Series();
    private XYChart.Series series5 = new XYChart.Series();

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

        series0.setName("0");
        series1.setName("1");
        series2.setName("2");
        series3.setName("3");
        series4.setName("4");
        series5.setName("5");
        popolaGrafico();

        barChart.getData().addAll(series0, series1, series2, series3, series4, series5);
    }

    private void popolaGrafico() {
        //TODO prendere eventi avversi da DB
        ArrayList<String> eventiAvversi = new ArrayList<String>();
        //Variabili per il conteggio e la media di tutti gli eventi avversi
        //MalDiTesta 0, Febbre 1, DMA 2, Linfo 3, Tachicardia 4, CrisiIper 5


        for(int i = 0; i < eventiAvversi.size();){
            inserisciColonna(eventiAvversi.get(i++), Integer.parseInt(eventiAvversi.get(i++)),
                    Math.round(Float.parseFloat(eventiAvversi.get(i++))));
        }

    }

    private void inserisciColonna(String nome, int cont, int severita){

        switch (severita){
            case 0:series0.getData().add(new XYChart.Data(nome, cont)); break;
            case 1:series1.getData().add(new XYChart.Data(nome, cont)); break;
            case 2:series2.getData().add(new XYChart.Data(nome, cont)); break;
            case 3:series3.getData().add(new XYChart.Data(nome, cont)); break;
            case 4:series4.getData().add(new XYChart.Data(nome, cont)); break;
            case 5:series5.getData().add(new XYChart.Data(nome, cont)); break;
        }
    }
}
