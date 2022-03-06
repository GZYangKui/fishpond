package cn.navclub.fishpond.app;

import cn.navclub.fishpond.app.controller.LoginController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        new LoginController().showAndFront();
    }
}
