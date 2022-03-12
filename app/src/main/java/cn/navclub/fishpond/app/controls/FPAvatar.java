package cn.navclub.fishpond.app.controls;

import cn.navclub.fishpond.app.util.FileUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class FPAvatar extends Circle {
    private static final String AVATAR_CLASS = "fp-avatar";

    private StringProperty url;
    private ImagePattern imagePattern;


    public FPAvatar() {
        this.setRadius(20);
        this.setCenterY(20);
        this.setCenterX(20);
        this.setStroke(Color.SEAGREEN);
        this.getStyleClass().add(AVATAR_CLASS);
        this.setEffect(new DropShadow(+25d, 0d, +2d, Color.DARKSEAGREEN));
        this.urlProperty().addListener(((observable, oldValue, newValue) -> {
            var pos = FileUtil.getResourcePos(newValue);
            if (pos == FileUtil.ResourcePos.LOCAL) {
                this.imagePattern = new ImagePattern(new Image(newValue));
                this.setFill(this.imagePattern);
            }
        }));
    }

    public final String getUrl() {
        return this.urlProperty().get();
    }

    public final void setUrl(String url) {
        this.urlProperty().set(url);
    }

    public StringProperty urlProperty() {
        if (url == null) {
            url = new SimpleStringProperty();
        }
        return url;
    }
}
