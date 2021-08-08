package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrazioneController {

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

    private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    private static final String COD_FISCALE_REGEX = "^[a-zA-Z]{6}[0-9]{2}[abcdehlmprstABCDEHLMPRST]{1}[0-9]{2}([a-zA-Z]{1}[0-9]{3})[a-zA-Z]{1}$";
    private static Pattern pattern;
    private Matcher matcher;


    public void indietro(MouseEvent mouseEvent) {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../view/loginLayout.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 300);
            Stage stage = new Stage();
            stage.setTitle("Login");
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

    public void registraCittadino(MouseEvent mouseEvent) {
        if(!verificaCampi()){
            return;
        }
        //Alert con id da dare al cittadino
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informazioni cittadino: " + codFiscale.getText());
        alert.setHeaderText(null);
        alert.setContentText("ID vaccinazione: " + id.getText() + ". Registrazione completata");
        alert.showAndWait();

        indietro(mouseEvent);
    }

    public void verificaId(MouseEvent mouseEvent) {
        if(!(id.getText().equals(""))){
            bottomPane.setDisable(false);
            topPane.setDisable(true);
        }
    }

    private boolean verificaCampi() {
        boolean verified = true;
        if (username.getText().equals(""))
            verified = setColorBorder(username, "red");
        else setColorBorder(username, "transparent");

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
                    Alert alertPassword = new Alert(Alert.AlertType.ERROR);
                    alertPassword.setTitle("");
                    alertPassword.setHeaderText("Errore nella compilazione dei campi");
                    alertPassword.setContentText("Le password non corrispondono");

                    alertPassword.showAndWait();
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
                Alert alertCod = new Alert(Alert.AlertType.ERROR);
                alertCod.setTitle("");
                alertCod.setHeaderText("Errore nella compilazione dei campi");
                alertCod.setContentText("Codice fiscale non valido");

                alertCod.showAndWait();
            }
        }

        if (email.getText().equals(""))
            verified = setColorBorder(email, "red");
        else {
            if (verificaEmail(email.getText()))
                setColorBorder(email, "transparent");
            else {
                verified = setColorBorder(email, "red");
                Alert alertEmail = new Alert(Alert.AlertType.ERROR);
                alertEmail.setTitle("");
                alertEmail.setHeaderText("Errore nella compilazione dei campi");
                alertEmail.setContentText("Email non valida");

                alertEmail.showAndWait();
            }
        }


        return verified;
    }

    private boolean verificaEmail(String email) {
        pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean verificaCodFiscale(String codFiscale) {
        pattern = Pattern.compile(COD_FISCALE_REGEX, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(codFiscale);
        return matcher.matches();
    }

    private boolean setColorBorder(Control component, String color){
        component.setStyle("-fx-border-color: " + color + ";");
        return false;
    }
}
