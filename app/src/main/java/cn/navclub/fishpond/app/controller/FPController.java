package cn.navclub.fishpond.app.controller;

import cn.navclub.fishpond.app.AssetsHelper;
import cn.navclub.fishpond.app.FXMLWinController;
import cn.navclub.fishpond.app.controller.component.TProPaneController;
import cn.navclub.fishpond.app.controls.FPAvatar;
import cn.navclub.fishpond.app.socket.SocketHolder;
import cn.navclub.fishpond.app.socket.SocketHook;
import cn.navclub.fishpond.app.socket.TCNStatus;
import cn.navclub.fishpond.app.util.DialogUtil;
import cn.navclub.fishpond.protocol.api.APIECode;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.model.TProMessage;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import lombok.Getter;
import org.controlsfx.control.Notifications;

public class FPController extends FXMLWinController<TabPane> implements SocketHook {
    private static final String DEFAULT_STYLE_CLASS = "nav-tab-pane";

    private final TProPaneController tProPaneController;

    private FPController() {
        super(new TabPane(), "鱼塘");

        this.tProPaneController = new TProPaneController();

        this.initUI();

        this.getStage().setWidth(910);
        this.getStage().setHeight(650);
        this.getStage().initStyle(StageStyle.TRANSPARENT);
        this.getStage().getScene().setFill(Color.TRANSPARENT);

        this.getParent().getStylesheets().add(AssetsHelper.loadStyleSheets("FPStyle.css"));

        SocketHolder.getInstance().addHook(this);

        this.getParent().getSelectionModel().selectedIndexProperty().addListener(((observable, oldValue, newValue) -> {

            var oi = TabItem.values()[oldValue.intValue()];
            var ni = TabItem.values()[newValue.intValue()];

            oi.getImageView().setImage(oi.icon);
            ni.getImageView().setImage(ni.active);
        }));
    }

    private void initUI() {
        this.getParent().setSide(Side.LEFT);
        this.getParent().getStyleClass().add(DEFAULT_STYLE_CLASS);
        this.getParent().setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        for (TabItem value : TabItem.values()) {
            var tab = new Tab();
            var func = new Label();
            tab.setTooltip(new Tooltip(value.text));
            var icon = new ImageView(value.selected ? value.active : value.icon);

            tab.setGraphic(func);
            func.setGraphic(icon);
            value.setImageView(icon);

            if (value == TabItem.MESSAGE) {
                tab.setContent(this.tProPaneController.getParent());
            }
            this.getParent().getTabs().add(tab);
        }
        this.getParent().getSelectionModel().select(0);
    }

    public void requestTCPConnect() {
        var holder = SocketHolder.getInstance();
        holder.setPort(9001);
        holder.setHost("127.0.0.1");
        holder.connect().onFailure(t -> Platform.runLater(() -> {
            DialogUtil.showEXDialog(t, "TCP连接错误");
            Platform.exit();
        }));
    }

    @Override
    public void onTCNStatusChange(TCNStatus oldValue, TCNStatus newValue) {
        Platform.runLater(() -> {
            var text = String.format("TCP连接状态发生改变(%s->%s)", oldValue.getText(), newValue.getText());
            Notifications.create()
                    .position(Pos.TOP_RIGHT)
                    .text(text)
                    .showInformation();
        });
    }

    @Override
    public void feedback(ServiceCode serviceCode, APIECode code, JsonObject content, TProMessage message) {
        //TCP注册不成功
        if (serviceCode == ServiceCode.TCP_REGISTER && code != APIECode.OK) {
            Platform.runLater(() -> {
                this.getStage().hide();
                new LoginController().showAndFront();
            });
        }
    }

    //00C268 A4a4a4
    @Getter
    private enum TabItem {
        MESSAGE(
                AssetsHelper.localIcon("message.png"),
                AssetsHelper.localIcon("message_selected.png"),
                "消息列表",
                true
        ),
        FRIEND(
                AssetsHelper.localIcon("friend.png"),
                AssetsHelper.localIcon("friend_selected.png"),
                "朋友列表",
                false
        );
        private final Image icon;
        private final Image active;
        private final String text;
        private final boolean selected;
        private ImageView imageView;

        TabItem(Image icon, Image active, String text, boolean selected) {
            this.icon = icon;
            this.active = active;
            this.text = text;
            this.selected = selected;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
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
