package cn.navclub.fishpond.app.controller;

import cn.navclub.fishpond.app.FXMLWinController;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

public class LoginController extends FXMLWinController<GridPane> {
    public LoginController() {
        super("Login.fxml", "用户登录");
        this.getScene().setFill(Color.TRANSPARENT);
        this.getStage().initStyle(StageStyle.TRANSPARENT);
    }
}
