package cn.navclub.fishpond.app.controls;

import cn.navclub.fishpond.core.config.Constant;
import cn.navclub.fishpond.protocol.enums.ContentType;
import io.vertx.core.json.JsonObject;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import static cn.navclub.fishpond.core.config.Constant.ITEMS;
import static cn.navclub.fishpond.core.config.Constant.MESSAGE;

public class TProTextFlow extends TextFlow {
    private static final String DEFAULT_CLASS = "fp-pro-text-flow";

    private final JsonObject message;

    public TProTextFlow(JsonObject message) {
        this.message = message;
        var arr = message.getJsonArray(ITEMS);
        for (Object o : arr) {
            var json = (JsonObject) o;
            var type = ContentType.getInstance(json.getInteger(Constant.TYPE));
            if (type == ContentType.UN_SUPPORT) {
                continue;
            }
            if (type == ContentType.PLAIN_TEXT) {
                var text = new Text(json.getString(MESSAGE));
                this.getChildren().add(text);
            }
        }
        this.getStyleClass().add(DEFAULT_CLASS);
    }
}
