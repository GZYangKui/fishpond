package cn.navclub.fishpond.app.controller.component;

import cn.navclub.fishpond.app.AbstractController;
import cn.navclub.fishpond.app.AssetsHelper;
import cn.navclub.fishpond.app.task.UDTask;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

import java.util.concurrent.atomic.AtomicInteger;

public class TaskUItemController<T> extends AbstractController<HBox> {
    @FXML
    private Label label;
    @FXML
    private Button btn;
    @FXML
    private Label timeLabel;
    @FXML
    private ProgressBar progress;

    private final UDTask<T> task;
    /**
     * 用于记录当前耗时
     */
    private final AtomicInteger counter;


    public TaskUItemController(UDTask<T> task, String taskName) {
        this.setParent(AssetsHelper.loadFXMLView("TaskUItem.fxml", this));
        this.task = task;
        this.label.setText(taskName);
        this.getParent().setUserData(this);
        this.counter = new AtomicInteger(0);
    }

    @Override
    protected void dispose() {
        //移除进度条宽度绑定
        this.progress.prefWidthProperty().unbind();
    }

    private void updateTText() {
        ;
        var counter = this.counter.get();
        //得到秒数
        var second = counter % 60;
        var minute = counter / 60 % 60;
        var hour = counter / 60 / 60;
        var day = hour % 24;
        var text = String.format("%02d:%02d:%02d:%02d", day, hour, minute, second);
        Platform.runLater(() -> this.timeLabel.setText(text));
    }

    public void updateTime(int delta) {
        //未处于运行中的任务不做处理
        if (task.getTkStatus() != UDTask.TKStatus.RUNNING) {
            return;
        }
        counter.getAndAdd(delta);
        this.updateTText();
    }
}
