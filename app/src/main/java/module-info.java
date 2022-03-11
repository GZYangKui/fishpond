module cn.navclub.fishpond.app {
    requires io.vertx.core;
    requires javafx.fxml;
    requires static lombok;
    requires javafx.controls;
    requires org.controlsfx.controls;
    requires io.vertx.web.client;
    requires cn.navclub.fishpond.core;
    requires cn.navclub.fishpond.protocol;
    requires com.fasterxml.jackson.databind;

    exports cn.navclub.fishpond.app;
    opens cn.navclub.fishpond.app.controller.component;
    opens cn.navclub.fishpond.app.controller to javafx.fxml;
    exports cn.navclub.fishpond.app.controller to javafx.fxml;
}