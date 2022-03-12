package cn.navclub.fishpond.app.controller;

import cn.navclub.fishpond.app.FXMLWinController;
import cn.navclub.fishpond.app.controller.component.ChatPaneController;
import cn.navclub.fishpond.app.controls.FPTabPane;
import cn.navclub.fishpond.app.socket.SocketHolder;
import cn.navclub.fishpond.app.socket.SocketHook;
import cn.navclub.fishpond.core.config.Constant;
import cn.navclub.fishpond.protocol.api.APIECode;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.model.TProMessage;
import javafx.application.Platform;
import javafx.scene.layout.BorderPane;

import static cn.navclub.fishpond.core.config.Constant.CODE;
import static cn.navclub.fishpond.core.config.Constant.CONTENT;

public class FPController extends FXMLWinController<FPTabPane> implements SocketHook {
    private final SocketHolder socketHolder;

    private FPController() {
        super("Fishpond.fxml", "鱼塘");
//        var chatPane = ChatPaneController.create(0);
        this.socketHolder = new SocketHolder("127.0.0.1", 9000);
        this.socketHolder.addHook(this);
//        this.socketHolder.addHook(chatPane);
//        this.getParent().setCenter(chatPane.getParent());
        this.socketHolder.connect();
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

    public SocketHolder getSocketHolder() {
        return socketHolder;
    }

    private static FPController controller;

    public synchronized static FPController getController() {
        if (controller == null) {
            controller = new FPController();
        }
        return controller;
    }
}
