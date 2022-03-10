package cn.navclub.fishpond.app.controller;

import cn.navclub.fishpond.app.FXMLWinController;
import cn.navclub.fishpond.app.socket.SocketHolder;
import cn.navclub.fishpond.app.socket.SocketHook;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.model.TProMessage;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.Notifications;

public class ChatRoomController extends FXMLWinController<BorderPane> implements SocketHook {
    private final SocketHolder socketHolder;

    private ChatRoomController() {
        super("ChatRoom.fxml", "Fishpond");
        this.socketHolder = new SocketHolder("127.0.0.1", 9000);
        this.socketHolder.addHook(this);
        this.socketHolder.connect();
    }

    @Override
    public void onMessage(TProMessage message) {
        if (message.getServiceCode() == ServiceCode.SYSTEM_NOTIFY) {
            this.showNotify(message);
        }
    }

    public void showNotify(TProMessage message) {
        Platform.runLater(() -> {
            Notifications
                    .create()
                    .position(Pos.TOP_RIGHT)
                    .text(message.getDataStr())
                    .showConfirm();
        });
    }

    public SocketHolder getSocketHolder() {
        return socketHolder;
    }

    private static ChatRoomController controller;

    public synchronized static ChatRoomController getController() {
        if (controller == null) {
            controller = new ChatRoomController();
        }
        return controller;
    }
}
