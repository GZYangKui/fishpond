package cn.navclub.fishpond.app.controller;

import cn.navclub.fishpond.app.FXMLWinController;
import cn.navclub.fishpond.app.controller.component.ChatPaneController;
import cn.navclub.fishpond.app.socket.SocketHolder;
import cn.navclub.fishpond.app.socket.SocketHook;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.model.TProMessage;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;

public class FPController extends FXMLWinController<BorderPane> implements SocketHook {
    private final SocketHolder socketHolder;

    private FPController() {
        super("Fishpond.fxml", "鱼塘");
        this.socketHolder = new SocketHolder("127.0.0.1", 9000);
        this.socketHolder.addHook(this);
        this.socketHolder.connect();
        this.getParent().setCenter(ChatPaneController.create(0).getParent());
    }

    @Override
    public void onMessage(TProMessage message) {
        if (message.getServiceCode() == ServiceCode.SYSTEM_NOTIFY) {
            this.showNotify(message);
        }
    }

    public void showNotify(TProMessage message) {
        Platform.runLater(() -> {
            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("系统通知");
            alert.setContentText(message.getDataStr());
            alert.setResizable(false);
            alert.showAndWait();
        });
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
