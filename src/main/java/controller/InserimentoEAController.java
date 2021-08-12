package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class InserimentoEAController {
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

    public void indietro(MouseEvent mouseEvent) {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../view/mainLayout.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 300);
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

    public void inserisciEA(MouseEvent mouseEvent) {
        //Salvo a DB le info
       indietro(mouseEvent);
    }

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
}
