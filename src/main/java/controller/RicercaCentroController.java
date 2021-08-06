package controller;

import datatypes.CentroVaccinale;
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

    public static CentroVaccinale centoVis;


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
            if (empty) {
                setGraphic(null);
            } else {
                customListCellController.setCentro(item);
                setGraphic(view);
            }
        }

    }

    private class CustomListCellController {
        @FXML
        private Label nomeCentro;
        @FXML
        private Label tipoCentro;
        @FXML
        private Label comuneCentro;

        private CentroVaccinale centro;

        private AnchorPane anchorPane;

        public CustomListCellController(){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/customListCell.fxml"));
                loader.setController(this);
                anchorPane = loader.load();
                anchorPane.addEventHandler(MouseEvent.MOUSE_PRESSED,
                        new EventHandler<MouseEvent>(){

                    public void handle(MouseEvent e) {
                        centoVis = centro;
                    }
                });
            } catch (IOException exc) {
                throw new UncheckedIOException(exc);
            }
        }

        public Node getView() {
            return anchorPane ;
        }

        public void setCentro(CentroVaccinale centro) {
            this.centro = centro;
            nomeCentro.setText(centro.getNome());
            tipoCentro.setText(centro.getTipologia());
            comuneCentro.setText(centro.getComune());
        }
    }
}
