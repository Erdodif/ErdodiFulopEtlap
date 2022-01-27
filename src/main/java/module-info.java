module hu.petrik.etlap {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens hu.petrik.etlap to javafx.fxml;
    exports hu.petrik.etlap;
    exports hu.petrik.etlap.controllers;
    opens hu.petrik.etlap.controllers to javafx.fxml;
    exports hu.petrik.etlap.db;
    opens hu.petrik.etlap.db to javafx.fxml;
}