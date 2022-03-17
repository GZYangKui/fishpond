package cn.navclub.fishpond.app.controls;

import cn.navclub.fishpond.app.AssetsHelper;
import javafx.scene.effect.DropShadow;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;


public class FPAvatar {
    private static final String AVATAR_CLASS = "fp-avatar";
    //系统用户默认头像
    public static final Image DEF_SYS_AVATAR = AssetsHelper.localIcon("user/sys.png");
    //默认用户头像
    private static final Image DEF_USER_AVATAR = AssetsHelper.localIcon("user/user.png");
    //默认组头像
    private static final Image DEF_GROUP_AVATAR = AssetsHelper.localIcon("user/group.png");

    private final Shape shape;


    public FPAvatar(double radius, double x, double y) {
        var circle = new Circle();

        circle.setCenterY(x);
        circle.setCenterX(y);
        circle.setRadius(radius);

        this.shape = circle;

        this.initAvatar();
    }

    public FPAvatar(double w, double h, double aw, double ah, boolean effect) {
        var rect = new Rectangle();

        rect.setWidth(w);
        rect.setHeight(h);
        rect.setArcWidth(aw);
        rect.setArcHeight(ah);

        this.shape = rect;
        if (effect) {
            this.initAvatar();
        }
    }

    private void initAvatar() {
        this.shape.setStroke(Color.SEAGREEN);
        this.shape.getStyleClass().add(AVATAR_CLASS);
        this.shape.setEffect(new DropShadow(+25d, 0d, +2d, Color.DARKSEAGREEN));
    }

    public FPAvatar setAvatar(Image image) {
        this.shape.setFill(new ImagePattern(image));
        return this;
    }

    public static FPAvatar group() {
        return new FPAvatar(45, 45, 10, 10, true).setAvatar(DEF_GROUP_AVATAR);
    }

    public static FPAvatar user() {
        return new FPAvatar(45, 45, 10, 10, true).setAvatar(DEF_USER_AVATAR);
    }
    public static FPAvatar system() {
        return new FPAvatar(45, 45, 10, 10, true).setAvatar(DEF_SYS_AVATAR);
    }

    public Shape getShape() {
        return shape;
    }
}