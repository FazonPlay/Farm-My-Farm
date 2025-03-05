module com.fazonplay.farmmyfarm.farmmyfarm {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.fazonplay.farmmyfarm to javafx.fxml;
    opens com.fazonplay.farmmyfarm.Controllers to javafx.fxml;
    exports com.fazonplay.farmmyfarm.Application;
    exports com.fazonplay.farmmyfarm.Controllers;

}