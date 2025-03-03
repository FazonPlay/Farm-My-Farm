module com.fazonplay.farmmyfarm.farmmyfarm {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.fazonplay.farmmyfarm to javafx.fxml;
    exports com.fazonplay.farmmyfarm;
}