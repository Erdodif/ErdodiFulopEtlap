package hu.petrik.etlap.controllers;

import hu.petrik.etlap.db.Etel;
import hu.petrik.etlap.db.EtlapDB;
import hu.petrik.etlap.db.Kategoria;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MainController extends Controller{
    public TableView<Etel> etelTable;
    public TableColumn<Etel, String> colNev;
    public TableColumn<Etel, String> colLeiras;
    public TableColumn<Etel, Integer> colAr;
    public TableColumn<Etel, Kategoria> colKategoria;
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
        } catch (Exception e) {
            smallError(e.getMessage());
        }
    }

    private void loadDB() {
        try {
            List<Etel> etelek = db.getEtelek();
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

}