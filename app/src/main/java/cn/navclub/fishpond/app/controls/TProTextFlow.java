package cn.navclub.fishpond.app.controls;

import cn.navclub.fishpond.core.config.Constant;
import cn.navclub.fishpond.protocol.enums.ContentType;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import static cn.navclub.fishpond.core.config.Constant.MESSAGE;

public class TProTextFlow extends TextFlow {
    private final JsonArray array;

    public TProTextFlow(JsonArray array) {
        this.array = array;
        for (Object o : this.array) {
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
    }
}
