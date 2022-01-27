package hu.petrik.etlap.controllers;

import hu.petrik.etlap.db.Etel;
import hu.petrik.etlap.db.EtlapDB;
import hu.petrik.etlap.db.Kategoria;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditController extends Controller{
    public ChoiceBox<Kategoria> kategoriaChoiceBox;
    public Spinner<Integer> arSpinner;
    public TextField leirasTextField;
    public TextField nevTextField;
    private Etel modositando;

    public void initialize(){
        List<Kategoria> kategoriak = Kategoria.getKategoriak();
        kategoriaChoiceBox.getItems().addAll(kategoriak);
    }

    @FXML
    public void onSaveButtonClick(ActionEvent actionEvent) {
        String nev = nevTextField.getText();
        String leiras = leirasTextField.getText();
        int kategoriaIndex = kategoriaChoiceBox.getSelectionModel().getSelectedIndex();
        if (nev.isEmpty()) {
            smallError("A név megadása kötelező!");
            return;
        }
        if (leiras.isEmpty()) {
            smallError("A kategória megadása kötelező!");
            return;
        }
        try {
            Integer ar = arSpinner.getValue();
            EtlapDB db = new EtlapDB();
            if (kategoriaIndex == -1) {
                smallAlert("Értékelés megadása kötelező!");
                return;
            }
            Kategoria kategoria = kategoriaChoiceBox.getValue();
            modositando.setNev(nev);
            modositando.setLeiras(leiras);
            modositando.setAr(ar);
            modositando.setKategoria(kategoria);
            boolean siker = db.editEtel(modositando);
            if (siker) {
                smallAlert("Sikeres módosítás!");
                this.stage.close();
                return;
            }
            smallAlert("Sikertelen módosítás!");
        } catch (NullPointerException e) {
            smallError("Az ár megadása kötelező!");
        } catch (SQLException e) {
            smallError("SQL Hiba: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            smallError("Az ár egy 300 és 300000 közötti egész számnak kell lennie!");
        }
    }

    public void setModositando(Etel modositando) {
        this.modositando = modositando;
        nevTextField.setText(modositando.getNev());
        leirasTextField.setText(modositando.getLeiras());
        arSpinner.getEditor().setText(Integer.toString(modositando.getAr()));
        kategoriaChoiceBox.setValue(modositando.getKategoria());
    }

    public Etel getModositando() {
        return this.modositando;
    }
}
