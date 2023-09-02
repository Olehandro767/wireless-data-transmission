module ua.edu.ontu.wdt {
    requires javafx.fxml;
    requires java.desktop;
    requires kotlin.stdlib;
    requires javafx.controls;
    requires org.yaml.snakeyaml;
    requires org.apache.logging.log4j;
//    requires org.apache.logging.log4j.core;
//    requires kotlinx.coroutines.core;

    opens ua.edu.ontu.wdt.controller to javafx.fxml;
    opens ua.edu.ontu.wdt.controller.template to javafx.fxml;
    exports ua.edu.ontu.wdt;
}