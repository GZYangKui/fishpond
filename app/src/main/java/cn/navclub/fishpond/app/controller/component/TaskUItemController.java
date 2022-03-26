package cn.navclub.fishpond.app.controller.component;

import cn.navclub.fishpond.app.AbstractController;
import cn.navclub.fishpond.app.AssetsHelper;
import cn.navclub.fishpond.app.task.UDTask;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

public class TaskUItemController<T> extends AbstractController<HBox> {
    @FXML
    private Label label;
    @FXML
    private Button btn;
    @FXML
    private ProgressBar progress;

    public TaskUItemController(UDTask<T> task, final String taskName) {
        this.setParent(AssetsHelper.loadFXMLView("TaskUItem.fxml", this));
        this.label.setText(taskName);
        this.progress.prefWidthProperty().bind(this.getParent().widthProperty().multiply(.89));
    }
}
