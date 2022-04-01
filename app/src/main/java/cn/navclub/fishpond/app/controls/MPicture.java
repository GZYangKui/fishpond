package cn.navclub.fishpond.app.controls;

import cn.navclub.fishpond.core.config.Constant;
import io.vertx.core.json.JsonObject;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 *
 * 图片消息控制器
 *
 */
public class MPicture {
    private final ImageView imageView;

    private MPicture(JsonObject json) {
        var thumbnail = json.getJsonObject(Constant.THUMBNAIL);
        this.imageView = new ImageView(
                new Image(
                        thumbnail.getString(Constant.URL),
                        true
                )
        );

        //放大图片
        this.imageView.setOnMouseClicked(event -> {

        });
    }

    public Node getControl() {
        return imageView;
    }

    public static MPicture create(JsonObject json) {
        return new MPicture(json);
    }

}
