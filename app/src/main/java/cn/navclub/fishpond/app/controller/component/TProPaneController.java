package cn.navclub.fishpond.app.controller.component;

import cn.navclub.fishpond.app.AbstractController;
import cn.navclub.fishpond.app.controls.XWinControl;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class TProPaneController extends AbstractController<BorderPane> {
    private final ListView<String> listView;
    private final ChatPaneController paneController;

    public TProPaneController() {
        this.setParent(new BorderPane());


        this.listView = new ListView<>();
        this.paneController = ChatPaneController.create(0);

        var vBox = new VBox();
        this.getParent().setCenter(vBox);
        this.getParent().setLeft(this.listView);

        vBox.getChildren().add(XWinControl.create().getParent());
        vBox.getChildren().add(paneController.getParent());
        VBox.setVgrow(paneController.getParent(), Priority.ALWAYS);
    }
}
