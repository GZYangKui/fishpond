package cn.navclub.fishpond.app.controller.component;

import cn.navclub.fishpond.app.AbstractController;
import cn.navclub.fishpond.app.AssetsHelper;
import cn.navclub.fishpond.app.controller.FPController;
import cn.navclub.fishpond.app.controls.TProWrapper;
import cn.navclub.fishpond.app.model.UPFileInfo;
import cn.navclub.fishpond.app.socket.SocketHolder;
import cn.navclub.fishpond.app.socket.SocketHook;
import cn.navclub.fishpond.app.task.TSubscribe;
import cn.navclub.fishpond.app.task.UDPoolExecutor;
import cn.navclub.fishpond.app.task.impl.UploadTask;
import cn.navclub.fishpond.app.util.TProUtil;
import cn.navclub.fishpond.core.util.StrUtil;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.model.TProMessage;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.util.List;


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
        TProUtil.sendPlainText(this.account, text)
                .onSuccess(v -> Platform.runLater(this.textArea::clear));
    }

    @FXML
    public void selectFile() {
        var file = FPController.getController().openFChooser("请选择文件");
        if (file == null) {
            return;
        }
        var task = new UploadTask(file);
        task.subscribe(new TSubscribe<>() {
            @Override
            public void complete(List<UPFileInfo> items) {
                System.out.println(items);
            }
        });
        //执行上传任务
        UDPoolExecutor.getInstance().execute(task);
    }

    @FXML
    private void selectPicture() {
        var filter = new FileChooser.ExtensionFilter(
                "JPG/PNG",
                "*.jpg",
                "*.png",
                "*.jpeg"
        );
        var file = FPController.getController().openFChooser("请选择图片", filter);
        if (file == null) {
            return;
        }
        var task = new UploadTask(file, true, 100, 100);
        task.subscribe(new TSubscribe<List<UPFileInfo>>() {
            @Override
            public void complete(List<UPFileInfo> item) {
                System.out.println(item);
            }
        });
        UDPoolExecutor.getInstance().execute(task);
    }

    public static ChatPaneController create(Integer account) {
        return new ChatPaneController(account);
    }
}
