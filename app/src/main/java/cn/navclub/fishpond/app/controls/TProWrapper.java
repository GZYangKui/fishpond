package cn.navclub.fishpond.app.controls;

import cn.navclub.fishpond.app.AssetsHelper;
import cn.navclub.fishpond.app.http.HTTPUtil;
import cn.navclub.fishpond.core.config.Constant;
import cn.navclub.fishpond.core.config.SysProperty;
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


public class TProWrapper extends HBox {
    private static final String USER_BOX_CLASS = "fp-user-box";
    private static final String MESSAGE_BOX_CLASS = "fp-tpro-box";

    private final TProTextFlow textFlow;
    private final TProMessage message;
    private final FPAvatar avatar;
    //判断是否当前登录用户信息
    private final boolean current;

    public TProWrapper(TProMessage message) {
        this.message = message;
        this.avatar = this.avatar();
        this.getStyleClass().add(MESSAGE_BOX_CLASS);
        this.current = message.getFrom().equals(HTTPUtil.getUsername());
        this.textFlow = new TProTextFlow(message.toJson(), current);
        this.initWrapper();

    }

    private FPAvatar avatar() {
        final FPAvatar avatar;
        if (this.message.getFrom() == SysProperty.SYS_ID) {
            avatar = FPAvatar.system();
        } else {
            avatar = FPAvatar.user();
        }
        return avatar;
    }

    private void initWrapper() {
        var vBox = new VBox();
        vBox.getChildren().add(textFlow);
        vBox.getStyleClass().add(USER_BOX_CLASS);
        var serviceCode = this.message.getServiceCode();
        if (serviceCode == ServiceCode.GROUP_MESSAGE) {
            var nickname = "系统通知";
            var from = this.message.getFrom();
            if (from != 0) {
                nickname = from.toString();
            }
            vBox.getChildren().add(0, new Label(nickname));
        }
        this.getChildren().add(avatar.getShape());

        if (current) {
            vBox.setAlignment(Pos.CENTER_RIGHT);
            this.getChildren().add(0, vBox);
        } else {
            vBox.setAlignment(Pos.CENTER_LEFT);
            this.getChildren().add(vBox);
        }
    }

    public boolean isCurrent() {
        return current;
    }
}
