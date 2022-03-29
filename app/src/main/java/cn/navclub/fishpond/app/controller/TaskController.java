package cn.navclub.fishpond.app.controller;

import cn.navclub.fishpond.app.AssetsHelper;
import cn.navclub.fishpond.app.FXMLWinController;
import cn.navclub.fishpond.app.controller.component.TaskUItemController;
import cn.navclub.fishpond.app.task.impl.UploadTask;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class TaskController extends FXMLWinController<BorderPane> {
    @FXML
    private TabPane tabPane;
    @FXML
    private ListView<HBox> UDLView;
    @FXML
    private ListView<HBox> DNLView;

    private final Timer timer;

    private final ChangeListener<Number> selectedListener;

    public TaskController() {
        super("Task.fxml", "后台任务");

        this.selectedListener = selectedListener();

        for (int i = 0; i < this.tabPane.getTabs().size(); i++) {

            var item = TabItem.values()[i];
            var label = new Label();

            final Image image;
            if (i == 0) {
                image = item.active;
            } else {
                image = item.icon;
            }

            label.setGraphic(label);
            label.setGraphic(new ImageView(image));
            label.setTooltip(new Tooltip(item.title));

            this.tabPane.getTabs().get(i).setGraphic(label);
        }

        this.timer = taskTimer();
        this.getStage().setResizable(false);

        this.tabPane.getSelectionModel().selectedIndexProperty().addListener(this.selectedListener);
    }

    private Timer taskTimer() {
        var timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                var items = new ArrayList<HBox>();

                items.addAll(UDLView.getItems());
                items.addAll(DNLView.getItems());

                for (HBox item : items) {
                    ((TaskUItemController<?>) item.getUserData()).updateTime(1);
                }
            }
        }, 0, 1000);
        return timer;
    }

    private ChangeListener<Number> selectedListener() {
        return ((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                var index = oldValue.intValue();
                var tab = this.tabPane.getTabs().get(index);
                ((Label) tab.getGraphic()).setGraphic(new ImageView(TabItem.values()[index].icon));
            }

            var index = newValue.intValue();
            var tab = this.tabPane.getTabs().get(index);
            ((Label) tab.getGraphic()).setGraphic(new ImageView(TabItem.values()[index].active));
        });
    }

    public void addULItem(File file) {
        var task = new UploadTask(file, "https://baidu.com");
        var item = new TaskUItemController<>(task, "下载文件");
        this.UDLView.getItems().add(item.getParent());
    }

    private enum TabItem {
        UPLOAD(
                AssetsHelper.localIcon("upload.png"),
                AssetsHelper.localIcon("upload_selected.png"),
                "上传任务"
        ),
        DOWNLOAD(
                AssetsHelper.localIcon("down.png"),
                AssetsHelper.localIcon("down_selected.png"),
                "下载任务");
        final Image icon;
        final Image active;
        final String title;

        TabItem(Image icon, Image active, String title) {
            this.icon = icon;
            this.active = active;
            this.title = title;
        }
    }


    private static TaskController controller;

    public static TaskController getInstance() {
        synchronized (TaskController.class) {
            if (controller == null) {
                controller = new TaskController();
            }
            return controller;
        }
    }
}
