package cn.navclub.fishpond.app.controls;

import cn.navclub.fishpond.app.AssetsHelper;
import cn.navclub.fishpond.app.controller.component.ChatPaneController;
import cn.navclub.fishpond.app.controller.component.TProPaneController;
import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import lombok.Getter;

public class FPTabPane extends TabPane {
    private static final String DEFAULT_STYLE_CLASS = "nav-tab-pane";


    public FPTabPane() {
        this.initUI();
        this.setSide(Side.LEFT);
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    }

    private void initUI() {
        for (TabItem value : TabItem.values()) {
            var tab = new Tab();
            var hBox = new HBox();
            var avatar = new FPAvatar(45, 45, 10, 10, false);
            tab.setGraphic(hBox);
            tab.setTooltip(new Tooltip(value.text));
            hBox.getChildren().add(avatar.getShape());
            avatar.setAvatar(AssetsHelper.localIcon(value.icon));
            if (value == TabItem.MESSAGE) {
                tab.setContent(new TProPaneController().getParent());
            }
            this.getTabs().add(tab);
        }
        this.getSelectionModel().select(1);
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
}
