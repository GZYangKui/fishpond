package cn.navclub.fishpond.app.controller.component;

import cn.navclub.fishpond.app.AbstractController;
import cn.navclub.fishpond.app.AssetsHelper;
import cn.navclub.fishpond.app.controller.FPController;
import cn.navclub.fishpond.app.controls.TProWrapper;
import cn.navclub.fishpond.app.socket.SocketHook;
import cn.navclub.fishpond.core.config.Constant;
import cn.navclub.fishpond.core.config.SysProperty;
import cn.navclub.fishpond.core.util.StrUtil;
import cn.navclub.fishpond.protocol.enums.ContentType;
import cn.navclub.fishpond.protocol.enums.MessageT;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.model.TProMessage;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import static cn.navclub.fishpond.core.config.Constant.MESSAGE;


public class ChatPaneController extends AbstractController<BorderPane> implements SocketHook {
    private final Integer account;

    @FXML
    private VBox viewPort;
    @FXML
    private TextArea textArea;
    @FXML
    private ScrollPane tProWin;


    public ChatPaneController(Integer account) {
        this.account = account;
        this.setParent(AssetsHelper.loadFXMLView("ChatPane.fxml", this));
    }

    @Override
    public void onMessage(TProMessage message) {
        if (message.getServiceCode() == ServiceCode.GROUP_MESSAGE) {
            Platform.runLater(() -> {
                var wrapper = new TProWrapper(message);
                this.viewPort.getChildren().add(wrapper);
            });
        }
    }

    @FXML
    public void sendMessage() {
        var text = this.textArea.getText();
        if (StrUtil.isEmpty(text)) {
            return;
        }
        var tPro = new TProMessage();
        tPro.setUuid(StrUtil.uuid());
        tPro.setType(MessageT.JSON);
        tPro.setTo(SysProperty.SYS_ID);
        tPro.setFrom(SysProperty.SYS_ID);
        tPro.setServiceCode(ServiceCode.GROUP_MESSAGE);
        var data = new JsonObject();
        data.put(Constant.TIMESTAMP, System.currentTimeMillis());
        data.put(Constant.ITEMS, new JsonArray()
                .add(new JsonObject()
                        .put(Constant.TYPE, ContentType.PLAIN_TEXT)
                        .put(MESSAGE, this.textArea.getText()))
        );
        tPro.setData(data.toBuffer());
        //写入消息
        FPController
                .getController()
                .getSocketHolder()
                .write(tPro)
                .onSuccess(ar -> {
                    Platform.runLater(() -> this.textArea.clear());
                });
    }

    public static ChatPaneController create(Integer account) {
        return new ChatPaneController(account);
    }
}
