package cn.navclub.fishpond.app.controls;

import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class FPTabPane extends TabPane {
    public FPTabPane() {
        this.setSide(Side.LEFT);
        this.getTabs().add(new Tab("测试"));
        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    }
}
