package cn.navclub.fishpond.app.controls;

import cn.navclub.fishpond.core.config.Constant;
import cn.navclub.fishpond.core.util.DateUtil;
import cn.navclub.fishpond.protocol.enums.ContentType;
import io.vertx.core.json.JsonObject;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


import static cn.navclub.fishpond.core.config.Constant.ITEMS;
import static cn.navclub.fishpond.core.config.Constant.MESSAGE;

public class TProTextFlow extends VBox {

    private static final String DEFAULT_TIME_BOX_CLASS = "fp-pro-time-box";
    private static final String DEFAULT_STYLE_CLASS = "fp-pro-text-flow-box";

    public TProTextFlow(JsonObject message, boolean current) {
        var textFlow = new TextFlow();

        var arr = message.getJsonArray(ITEMS);
        for (Object o : arr) {
            var json = (JsonObject) o;
            var type = ContentType.getInstance(json.getInteger(Constant.TYPE));
            if (type == ContentType.UN_SUPPORT) {
                continue;
            }
            if (type == ContentType.PLAIN_TEXT) {
                var text = new Text(json.getString(MESSAGE));
                textFlow.getChildren().add(text);
            }
            if (type == ContentType.IMG) {
                var img = new ImageView(new Image(
                        json.getString("message"),
                        250,
                        250,
                        false,
                        true,
                        true)
                );
                textFlow.getChildren().add(img);
            }
        }

        var hBox = new HBox();
        var timestamp = message.getLong(Constant.TIMESTAMP);
        var label = new Label(DateUtil.formatDateTime(timestamp, "HH:mm"));
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().add(label);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        hBox.getStyleClass().add(DEFAULT_TIME_BOX_CLASS);

        this.getChildren().addAll(textFlow, hBox);
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }
}
