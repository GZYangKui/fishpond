package cn.navclub.fishpond.app.controls;

import cn.navclub.fishpond.app.socket.SocketHolder;
import cn.navclub.fishpond.app.socket.SocketHook;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.model.TProMessage;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.util.Objects;
import java.util.Optional;

public class CListView extends ListView<CListView.ListItem> implements SocketHook {

    private static final String DEFAULT_STYLE_CLASS = "tpro-chat-list";
    private static final String DEFAULT_ITEM_CLASS = "tpro-chat-list-item";

    public CListView() {
        SocketHolder.getInstance().addHook(this);
        this.getItems().add(new ListItem("鱼塘", 0));
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    @Override
    public void onMessage(TProMessage tPro) {
        if (tPro.getServiceCode() != ServiceCode.GROUP_MESSAGE && tPro.getServiceCode() != ServiceCode.P2P_MESSAGE) {
            return;
        }
        for (ListItem item : this.getItems()) {
            var current = Objects.equals(item.getAccount(), tPro.getFrom());
            if (!current || Objects.equals(item.getAccount(), tPro.getTo())) {
                Platform.runLater(() -> {
                    item.getMessage().setText("新消息");
                });
                return;
            }
        }
    }

    @Getter
    public static class ListItem extends HBox {
        private final VBox vBox;
        private final FPAvatar avatar;
        private final Label username;
        private final Label message;
        private final Integer account;

        public ListItem(final String nickname, final Integer account) {
            this.account = account;
            this.vBox = new VBox();
            this.message = new Label();
            if (account == 0) {
                this.avatar = FPAvatar.group();
            } else {
                this.avatar = FPAvatar.user();
            }
            this.username = new Label(Optional.ofNullable(nickname).orElse(account.toString()));
            this.vBox.getChildren().addAll(username, message);
            this.getChildren().addAll(avatar.getShape(), vBox);

            this.getStyleClass().add(DEFAULT_ITEM_CLASS);
        }
    }
}
