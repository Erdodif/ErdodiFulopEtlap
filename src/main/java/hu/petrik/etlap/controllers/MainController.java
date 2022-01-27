package hu.petrik.etlap.controllers;

import hu.petrik.etlap.db.Etel;
import hu.petrik.etlap.db.EtlapDB;
import hu.petrik.etlap.db.Kategoria;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MainController extends Controller {
    public TableView<Etel> etelTable;
    public TableColumn<Etel, String> colNev;
    public TableColumn<Etel, String> colLeiras;
    public TableColumn<Etel, Integer> colAr;
    public TableColumn<Etel, Kategoria> colKategoria;
    public ChoiceBox<Kategoria> kategoriaChoiceBox;
    public Label emelesSzoveg;
    public Spinner<Integer> emelesSpinner;
    private Kategoria filter = Kategoria.EMPTY_CATEGORY;
    private EtlapDB db;

    public void initialize() throws SQLException {
        colNev.setCellValueFactory(new PropertyValueFactory<>("nev"));
        colLeiras.setCellValueFactory(new PropertyValueFactory<>("leiras"));
        colAr.setCellValueFactory(new PropertyValueFactory<>("ar"));
        colKategoria.setCellValueFactory(new PropertyValueFactory<>("kategoria"));
        try {
            this.db = new EtlapDB();
            Kategoria.initialize(db);
            loadDB();
            List<Kategoria> kategoriak = Kategoria.getKategoriak();
            kategoriaChoiceBox.getItems().add(Kategoria.EMPTY_CATEGORY);
            kategoriaChoiceBox.getItems().addAll(kategoriak);
            kategoriaChoiceBox.getSelectionModel().select(Kategoria.EMPTY_CATEGORY);
            kategoriaChoiceBox.onActionProperty().setValue(actionEvent -> {
                MainController.this.filter = MainController.this.kategoriaChoiceBox.getSelectionModel().getSelectedItem();
                MainController.this.loadDB();
            });
            emelesSpinner.valueProperty().addListener((ObservableValue<? extends Integer> obs, Integer oldV, Integer newV) -> {
                if (newV < 5) {
                    newV = 5;
                }
                if (newV > 3000) {
                    newV = 3000;
                }
                if (newV > 50) {
                    emelesSzoveg.setText("Ft-tal");
                } else {
                    emelesSzoveg.setText("%-kal");
                }
                emelesSpinner.getEditor().positionCaret(Integer.MAX_VALUE); // Végre a viselkedés, amit kerestem
            });
            emelesSpinner.getEditor().setOnAction(action -> { //Kikerülve a hibás adat létrejöttét
                String text = emelesSpinner.getEditor().getText();
                try {
                    if (text == null) {
                        throw new IllegalArgumentException("Come on");
                    }
                    Integer.parseInt(emelesSpinner.getEditor().getText());
                } catch (Exception e) {
                    emelesSpinner.getEditor().setText("5");
                }
                emelesSpinner.getEditor().positionCaret(Integer.MAX_VALUE);
                action.consume();
            });

        } catch (Exception e) {
            smallError(e.getMessage());
        }
    }

    private void loadDB() {
        try {
            List<Etel> etelek = db.getEtelek(filter);
            etelTable.getItems().clear();
            for (Etel etel : etelek) {
                etelTable.getItems().add(etel);
            }
        } catch (SQLException e) {
            exceptionAllert(e);
        }
    }

    @FXML
    public void onHozzaadasButtonClick(ActionEvent actionEvent) {
        try {
            Controller newWindow = newWindow("add-view.fxml", "Új Étel", 600, 200);
            newWindow.getStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    loadDB();
                }
            });
            newWindow.getStage().show();
        } catch (IOException e) {
            exceptionAllert(e);
        }
    }

    @FXML
    public void onModositasButtonClick(ActionEvent actionEvent) {
        int selectedIndex = etelTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            smallError("Nincs kiválasztva elem!");
            return;
        }
        Etel modositando = etelTable.getSelectionModel().getSelectedItem();
        try {
            EditController newWindow = (EditController) newWindow(
                    "edit-view.fxml", modositando.getNev() + " Módosítása", 600, 200);

            newWindow.getStage().setOnHiding(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    loadDB();
                }
            });
            newWindow.setModositando(modositando);
            newWindow.getStage().show();
        } catch (IOException e) {
            e.printStackTrace();
            exceptionAllert(e);
        }
    }

    @FXML
    public void onTorlesButtonClick(ActionEvent actionEvent) {
        int selectedIndex = etelTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            smallError("Nincs kiválasztva elem!");
            return;
        }
        Etel torlendoEtel = etelTable.getSelectionModel().getSelectedItem();
        if (!confirm("Biztos, hogy törölni szeretné az alábbi elemet?", "Az étel hash kódja: " + torlendoEtel.hashCode())) {
            return;
        }
        try {
            if (db.deleteEtel(torlendoEtel.getId())) {
                smallAlert("Sikeres törlés");
                loadDB();
                return;
            }
            smallAlert("Sikertelen törlés");
        } catch (Exception e) {
            exceptionAllert(e);
        }
    }

    public void onEmelesButtonClick(ActionEvent actionEvent) throws SQLException {
        if (emelesSpinner.getValue() == null) {
            smallError("Nincs emelés mérték beállítva!");
            emelesSpinner.getEditor().fireEvent(new ActionEvent()); //Kezdőérték beállítása hiba esetén a validátorral
            return;
        }
        int selectedIndex = etelTable.getSelectionModel().getSelectedIndex();
        Integer id = null;
        if (selectedIndex != -1) {
            id = etelTable.getSelectionModel().getSelectedItem().getId();
        }
        db.raisePrice(emelesSpinner.getValue(), id, filter);
        loadDB();
    }

    public void cancelSelect(ActionEvent actionEvent) {
        etelTable.getSelectionModel().clearSelection();
    }
}