package cn.navclub.fishpond.app.controller.component;

import cn.navclub.fishpond.app.AbstractController;
import cn.navclub.fishpond.app.AssetsHelper;
import cn.navclub.fishpond.app.controller.FPController;
import cn.navclub.fishpond.app.controls.TProWrapper;
import cn.navclub.fishpond.app.socket.SocketHolder;
import cn.navclub.fishpond.app.socket.SocketHook;
import cn.navclub.fishpond.app.task.UDPoolExecutor;
import cn.navclub.fishpond.app.task.impl.UploadTask;
import cn.navclub.fishpond.core.config.Constant;
import cn.navclub.fishpond.core.config.SysProperty;
import cn.navclub.fishpond.core.util.StrUtil;
import cn.navclub.fishpond.protocol.enums.ContentType;
import cn.navclub.fishpond.protocol.enums.MessageT;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.model.TProMessage;
import io.vertx.core.impl.launcher.commands.FileSelector;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import static cn.navclub.fishpond.core.config.Constant.MESSAGE;


public class ChatPaneController extends AbstractController<BorderPane> implements SocketHook {
    private final Integer account;

    @FXML
    private VBox viewPort;
    @FXML
    private TextArea textArea;
    @FXML
    private ScrollPane tProWin;

    private final ChangeListener<Number> VPHListener;


    public ChatPaneController(Integer account) {
        this.account = account;
        SocketHolder.getInstance().addHook(this);
        this.setParent(AssetsHelper.loadFXMLView("ChatPane.fxml", this));
        this.viewPort.heightProperty().addListener((this.VPHListener = this.getVPHListener()));
    }

    private ChangeListener<Number> getVPHListener() {
        return (observable, oldValue, newValue) -> this.tProWin.setVvalue(1d);
    }

    @Override
    protected void dispose() {
        this.viewPort.heightProperty().removeListener(this.VPHListener);
    }

    @Override
    public void onMessage(TProMessage message) {
        if (message.getServiceCode() == ServiceCode.GROUP_MESSAGE) {
            Platform.runLater(() -> {
                var wrapper = new TProWrapper(message);
                var hBox = new HBox();
                if (wrapper.isCurrent()) {
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                } else {
                    hBox.setAlignment(Pos.CENTER_LEFT);
                }
                hBox.getChildren().add(wrapper);
                this.viewPort.getChildren().add(hBox);
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
        tPro.setTo(this.account);
        tPro.setUuid(StrUtil.uuid());
        tPro.setType(MessageT.JSON);
        tPro.setFrom(SysProperty.SYS_ID);
        tPro.setServiceCode(ServiceCode.GROUP_MESSAGE);
        var data = new JsonObject();
        data.put(Constant.TIMESTAMP, System.currentTimeMillis());
        data.put(Constant.ITEMS, new JsonArray()
                .add(new JsonObject()
                        .put(Constant.TYPE, ContentType.PLAIN_TEXT.getValue())
                        .put(MESSAGE, this.textArea.getText()))
        );
        tPro.setData(data.toBuffer());
        //写入消息
        SocketHolder
                .getInstance()
                .write(tPro)
                .onSuccess(ar -> Platform.runLater(() -> this.textArea.clear()));
    }

    @FXML
    public void selectFile() {
        var selector = new FileChooser();
        var file = selector.showOpenDialog(FPController.getController().getStage());
        if (file == null) {
            return;
        }
        //执行上传任务
        UDPoolExecutor.getInstance().execute(new UploadTask(file));
    }

    public static ChatPaneController create(Integer account) {
        return new ChatPaneController(account);
    }
}
