package cn.navclub.fishpond.app.controller;

import cn.navclub.fishpond.app.FXMLWinController;
import cn.navclub.fishpond.app.http.API;
import cn.navclub.fishpond.app.http.HTTPUtil;
import cn.navclub.fishpond.core.util.StrUtil;
import io.vertx.core.json.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

import static cn.navclub.fishpond.core.config.Constant.*;


public class LoginController extends FXMLWinController<GridPane> {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    public LoginController() {
        super("Login.fxml", "用户登录");
        this.getScene().setFill(Color.TRANSPARENT);
        this.getStage().initStyle(StageStyle.TRANSPARENT);
    }

    @FXML
    public void login() {
        var u = this.username.getText();
        var p = this.password.getText();
        if (StrUtil.isEmpty(u) || StrUtil.isEmpty(p)) {
            return;
        }
        var data = new JsonObject();
        data.put(USERNAME, Integer.valueOf(u));
        data.put(PASSWORD, p);
        var future = HTTPUtil.<JsonObject>doPost(API.REQ_LOGIN, null, data);
        future.onSuccess(json -> {
            HTTPUtil.setSessionId(json.getString(SESSION_ID));
            this.toFixWindow(FPController.getController());
        });
    }
}
