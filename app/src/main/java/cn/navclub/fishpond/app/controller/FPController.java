package cn.navclub.fishpond.app.controller;

import cn.navclub.fishpond.app.AssetsHelper;
import cn.navclub.fishpond.app.FXMLWinController;
import cn.navclub.fishpond.app.controller.component.TProPaneController;
import cn.navclub.fishpond.app.controls.FPAvatar;
import cn.navclub.fishpond.app.socket.SocketHolder;
import cn.navclub.fishpond.app.socket.SocketHook;
import cn.navclub.fishpond.app.util.DialogUtil;
import cn.navclub.fishpond.core.config.Constant;
import cn.navclub.fishpond.protocol.api.APIECode;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.model.TProMessage;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import lombok.Getter;

import static cn.navclub.fishpond.core.config.Constant.CODE;
import static cn.navclub.fishpond.core.config.Constant.CONTENT;

public class FPController extends FXMLWinController<TabPane> implements SocketHook {
    private static final String DEFAULT_STYLE_CLASS = "nav-tab-pane";

    private final TProPaneController tProPaneController;

    private FPController() {
        super(new TabPane(), "鱼塘");

        this.tProPaneController = new TProPaneController();

        this.initUI();

        this.requestTCPConnect();

        this.getStage().setWidth(910);
        this.getStage().setHeight(650);
        this.getStage().initStyle(StageStyle.TRANSPARENT);
        this.getStage().getScene().setFill(Color.TRANSPARENT);

        this.getParent().getStylesheets().add(AssetsHelper.loadStyleSheets("FPStyle.css"));

        SocketHolder.getInstance().addHook(this);
    }

    private void initUI() {
        this.getParent().setSide(Side.LEFT);
        this.getParent().getStyleClass().add(DEFAULT_STYLE_CLASS);
        this.getParent().setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        for (TabItem value : TabItem.values()) {
            var tab = new Tab();
            var hBox = new HBox();
            var avatar = new FPAvatar(45, 45, 10, 10, false);
            tab.setGraphic(hBox);
            tab.setTooltip(new Tooltip(value.text));
            hBox.getChildren().add(avatar.getShape());
            avatar.setAvatar(AssetsHelper.localIcon(value.icon));
            if (value == TabItem.MESSAGE) {
                tab.setContent(this.tProPaneController.getParent());
            }
            this.getParent().getTabs().add(tab);
        }
        this.getParent().getSelectionModel().select(1);
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
                SocketHolder.getInstance().plus();
            } else {
                Platform.runLater(() -> {
                    this.getStage().hide();
                    new LoginController().showAndFront();
                });
            }
        }
    }

    @Getter
    private enum TabItem {
        PERSONAL("sys_user.png", "个人信息"),
        MESSAGE("message.png", "消息列表"),
        FRIEND("friend.png", "朋友列表");
        private final String icon;
        private final String text;

        TabItem(String icon, String text) {
            this.icon = icon;
            this.text = text;
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
