package cn.navclub.fishpond.app;

import cn.navclub.fishpond.app.controller.LoginController;
import io.vertx.core.Vertx;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static final Vertx vertx = Vertx.vertx();

    @Override
    public void start(Stage primaryStage) throws Exception {
        new LoginController().showAndFront();
    }

    @Override
    public void stop() throws Exception {
        Main.vertx.close();
    }
}
