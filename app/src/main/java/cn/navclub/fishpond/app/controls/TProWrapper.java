package cn.navclub.fishpond.app.controls;

import cn.navclub.fishpond.app.AssetsHelper;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.model.TProMessage;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;


public class TProWrapper extends VBox {
    private static final String AVATAR_CLASS = "fp-avatar";
    private static final String USER_BOX_CLASS = "fp-user-box";
    private static final String MESSAGE_BOX_CLASS = "fp-tpro-box";

    private final TProTextFlow textFlow;
    private final TProMessage message;
    private  Circle avatar;

    public TProWrapper(TProMessage message) {
        this.avatar();
        this.message = message;
        this.textFlow = new TProTextFlow(message.toJson());
        this.initWrapper();
        this.getStyleClass().add(MESSAGE_BOX_CLASS);
    }

    private void avatar() {
        this.avatar = new Circle(20, 20, 20);
        this.avatar.setStroke(Color.SEAGREEN);
        this.avatar.getStyleClass().add(AVATAR_CLASS);
        this.avatar.setFill(new ImagePattern(AssetsHelper.localIcon("sys_notify.png")));
        this.avatar.setEffect(new DropShadow(+25d, 0d, +2d, Color.DARKSEAGREEN));
    }

    private void initWrapper() {
        var hBox = new HBox();
        hBox.getChildren().add(avatar);
        hBox.getStyleClass().add(USER_BOX_CLASS);
        var serviceCode = this.message.getServiceCode();
        if (serviceCode == ServiceCode.GROUP_MESSAGE) {
            var nickname = "系统通知";
            var from = this.message.getFrom();
            if (from != 0) {
                nickname = from.toString();
            }
            var label = new Label(nickname);
            hBox.getChildren().add(label);
        }
        this.getChildren().add(hBox);
        this.getChildren().add(textFlow);
    }
}
