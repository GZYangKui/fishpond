package cn.navclub.fishpond.app.controls;

import cn.navclub.fishpond.protocol.model.TProMessage;
import javafx.scene.layout.HBox;

public class TProWrapper extends HBox {
    private final TProTextFlow textFlow;

    public TProWrapper(TProMessage message) {
        this.textFlow = new TProTextFlow(message.getData().toJsonArray());
    }
}
