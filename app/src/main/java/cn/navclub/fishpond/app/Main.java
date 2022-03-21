package cn.navclub.fishpond.app;

import cn.navclub.fishpond.app.controller.LoginController;
import io.vertx.core.Vertx;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main extends Application {

    public static final Vertx vertx = Vertx.vertx();

    public static void main(String[] args) {
        Main.launch(Main.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new LoginController().showAndFront();
    }

    @Override
    public void stop() throws Exception {
        Main.vertx.close();
    }
}
