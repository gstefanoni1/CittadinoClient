package controller;

import datatypes.CentroVaccinale;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class VisualizzaCentroController implements Initializable {
    @FXML
    private Label nomeCentro;
    @FXML
    private Label indirizzo;
    @FXML
    private Label tipologia;

    public void indietro(MouseEvent mouseEvent) {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../view/mainLayout.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 300);
            Stage stage = new Stage();
            stage.setTitle("Vaccinazioni Cittadini");
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
                RicercaCentroController.centoVis.getComune() + ", " +
                RicercaCentroController.centoVis.getCap() + ", " +
                RicercaCentroController.centoVis.getSiglaProvincia());
    }
}
