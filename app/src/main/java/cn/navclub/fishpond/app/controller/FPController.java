package cn.navclub.fishpond.app.controller;

import cn.navclub.fishpond.app.AssetsHelper;
import cn.navclub.fishpond.app.FXMLWinController;
import cn.navclub.fishpond.app.controls.FPTabPane;
import cn.navclub.fishpond.app.socket.SocketHolder;
import cn.navclub.fishpond.app.socket.SocketHook;
import cn.navclub.fishpond.app.util.DialogUtil;
import cn.navclub.fishpond.core.config.Constant;
import cn.navclub.fishpond.protocol.api.APIECode;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.model.TProMessage;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

import static cn.navclub.fishpond.core.config.Constant.CODE;
import static cn.navclub.fishpond.core.config.Constant.CONTENT;

public class FPController extends FXMLWinController<FPTabPane> implements SocketHook {

    private FPController() {
        super(new FPTabPane(), "鱼塘");

        this.requestTCPConnect();

        this.getStage().setWidth(910);
        this.getStage().setHeight(650);
        this.getStage().initStyle(StageStyle.TRANSPARENT);
        this.getStage().getScene().setFill(Color.TRANSPARENT);
        this.getParent().getStylesheets().add(AssetsHelper.loadStyleSheets("FPStyle.css"));
    }

    private void requestTCPConnect() {
        var holder = SocketHolder.getInstance();
        holder.setPort(9000);
        holder.setHost("127.0.0.1");
        holder.connect().onFailure(t -> Platform.runLater(() -> {
            DialogUtil.showEXDialog(t, "TCP连接错误");
            Platform.exit();
        }));
    }

    @Override
    public void onMessage(TProMessage message) {
        if (message.getServiceCode() == ServiceCode.OPERATE_FEEDBACK) {
            this.operateFeedBack(message);
        }
    }

    public void operateFeedBack(TProMessage message) {
        var json = message.toJson();
        var serviceCode = ServiceCode.serviceCode(json.getInteger(Constant.SERVICE_CODE));
        if (serviceCode == ServiceCode.TCP_REGISTER) {
            var content = json.getJsonObject(CONTENT);
            //开启心跳
            if (content.getInteger(CODE) == APIECode.OK.getCode()) {

            } else {
                Platform.runLater(() -> {
                    this.getStage().hide();
                    new LoginController().showAndFront();
                });
            }
        }
    }


    private static FPController controller;

    public synchronized static FPController getController() {
        if (controller == null) {
            controller = new FPController();
        }
        return controller;
    }
}
