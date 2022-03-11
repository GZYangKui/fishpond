package cn.navclub.fishpond.app.controller.component;

import cn.navclub.fishpond.app.AbstractController;
import cn.navclub.fishpond.app.AssetsHelper;
import javafx.scene.layout.BorderPane;


public class ChatPaneController extends AbstractController<BorderPane> {
    private final Integer account;

    public ChatPaneController(Integer account) {
        this.account = account;
        this.setParent(AssetsHelper.loadFXMLView("ChatPane.fxml", this));
    }

    public static ChatPaneController create(Integer account) {
        return new ChatPaneController(account);
    }
}
