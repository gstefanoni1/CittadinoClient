package controller;

import datatypes.CentroVaccinale;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RicercaCentroController implements Initializable {
    @FXML
    private ChoiceBox<String> typeSearch;
    @FXML
    private ChoiceBox<String> tipologia;
    @FXML
    private TextField textFilter;
    @FXML
    private ListView<CentroVaccinale> CentriList;


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
        typeSearch.getSelectionModel()
                .selectedItemProperty()
                .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue)
                        -> choiceEnable(newValue) );

        //Recuperare info
        ObservableList<CentroVaccinale> data = FXCollections.observableArrayList();
        CentriList = new ListView<CentroVaccinale>(data);
        CentriList.setCellFactory(new Callback<ListView<CentroVaccinale>, ListCell<CentroVaccinale>>() {
            @Override
            public ListCell<CentroVaccinale> call(ListView<CentroVaccinale> listView) {
                return new CustomListCell();
            }
        });
    }

    private void choiceEnable(String value){
        if(value.equals("Nome centro"))
            tipologia.setDisable(true);
        else
            tipologia.setDisable(false);
    }

    public void ricerca(MouseEvent mouseEvent) {
        if(typeSearch.getValue().equals("Nome centro"))
            ricercaPerNome();
        else
            ricercaPerComuneTipo();
    }

    public void cancella(MouseEvent mouseEvent) {
        textFilter.setText("");
    }

    public void ricercaPerNome(){

    }

    public void ricercaPerComuneTipo(){

    }

    private class CustomListCell extends ListCell<CentroVaccinale> {
        private final CustomListCellController customListCellController = new CustomListCellController();
        private final Node view = customListCellController.getView();
        public CustomListCell() {
            super();
        }

        @Override
        protected void updateItem(CentroVaccinale item, boolean empty) {
            super.updateItem(item, empty);
        }

    }

    private class CustomListCellController {
        private AnchorPane anchorPane;

        public CustomListCellController(){
            try {
                // assumes FXML file is in same package as this controller
                // (also make sure name of FXML resource is correct)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomListCell.fxml"));
                loader.setController(this);
                anchorPane = loader.load();
            } catch (IOException exc) {
                // pretty much fatal here...
                throw new UncheckedIOException(exc);
            }
        }

        public Node getView() {
            return anchorPane ;
        }
    }
}
