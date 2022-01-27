package hu.petrik.etlap.controllers;

import hu.petrik.etlap.db.Etel;
import hu.petrik.etlap.db.EtlapDB;
import hu.petrik.etlap.db.Kategoria;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.List;

public class AddController extends Controller {
    @FXML
    public Spinner<Integer> arSpinner;
    @FXML
    public ChoiceBox<Kategoria> kategoriaChoiceBox;
    @FXML
    public TextField nevTextField;
    @FXML
    public TextField leirasTextField;

    public void initialize() {
        List<Kategoria> kategoriak = Kategoria.getKategoriak();
        kategoriaChoiceBox.getItems().addAll(kategoriak);
    }

    @FXML
    public void onHozzadasButtonClick(ActionEvent actionEvent) {
        String nev = nevTextField.getText();
        String leiras = leirasTextField.getText();
        //Pattern pattern = Pattern.compile("\\p{javaAlphabetic}");
        //boolean rendben = Pattern.matches(String.valueOf(pattern),nev);
        int ertekelesIndex = kategoriaChoiceBox.getSelectionModel().getSelectedIndex();
        if (nev.isEmpty()) {
            smallError("A név megadása kötelező!");
            return;
        }
        if (leiras.isEmpty()) {
            smallError("A leírás megadása kötelező!");
            return;
        }
        try {
            Integer ar = arSpinner.getValue();
            EtlapDB db = new EtlapDB();
            if (ertekelesIndex == -1) {
                smallAlert("Értékelés megadása kötelező!");
                return;
            }
            Kategoria kategoria = kategoriaChoiceBox.getValue();
            boolean siker = db.addEtel(new Etel(0, nev, leiras, ar, kategoria.getId()));
            if (siker) {
                smallAlert("Sikeres hozzáadás!");
                return;
            }
            smallAlert("Sikertelen hozzáadás!");
        } catch (NullPointerException e) {
            smallError("A hossz megadása kötelező!");
        } catch (SQLException e) {
            smallError("SQL Hibe: " + e.getMessage());
        } catch (Exception e) {
            smallError("A hossznak egy 1 és 999 közötti egész számnak kell lennie!");
        }
    }

}
