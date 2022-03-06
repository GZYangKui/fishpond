package cn.navclub.fishpond.app.controller;

import cn.navclub.fishpond.app.FXMLWinController;
import javafx.scene.layout.BorderPane;

public class LoginController extends FXMLWinController<BorderPane> {
    public LoginController() {
        super("Login.fxml");
        this.getStage().setTitle("Login");
    }
}
