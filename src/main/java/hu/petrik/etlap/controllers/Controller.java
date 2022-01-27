package hu.petrik.etlap.controllers;


import hu.petrik.etlap.EtlapApp;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Controller {

    protected Stage stage;

    public Stage getStage() {
        return this.stage;
    }

    private void showAlert(Alert.AlertType type, String s, EventHandler<DialogEvent> handler) {
        Alert alert = new Alert(type);
        alert.setContentText(s);
        if (type == Alert.AlertType.NONE) {
            alert.getButtonTypes().add(ButtonType.OK);
        }
        if (handler != null) {
            alert.setOnCloseRequest(handler);
        }
        alert.show();
    }

    protected void smallAlert(String s) {
        showAlert(Alert.AlertType.NONE, s, null);
    }

    protected void smallError(String s) {
        showAlert(Alert.AlertType.ERROR, s, null);
    }

    protected void exitAlert(String s) {
        showAlert(Alert.AlertType.NONE, s, new EventHandler<DialogEvent>() {
            @Override
            public void handle(DialogEvent dialogEvent) {
                //TODO
            }
        });
    }

    protected void exceptionAllert(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hiba");
        alert.setHeaderText(e.getClass().toString());
        alert.setContentText(e.getMessage());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(alert::show);
            }
        }, 500);
    }

    protected boolean confirm(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("are you sure about it?");
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait().get().equals(ButtonType.OK);
    }

    protected void alertWait(String content) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setContentText(content);
        alert.showAndWait();
    }

    protected static Controller newWindow(String fxml, String title, int width, int height) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(EtlapApp.class.getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stage.setTitle(title);
        stage.setScene(scene);
        Controller controller = fxmlLoader.getController();
        controller.stage = stage;
        return controller;
    }
}