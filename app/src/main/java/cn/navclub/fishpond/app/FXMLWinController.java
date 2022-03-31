package cn.navclub.fishpond.app;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.nio.file.Path;

/**
 * FXML Window controller
 *
 * @param <T>
 */
public class FXMLWinController<T extends Parent> extends AbstractController<T> {
    private final Stage stage;

    public FXMLWinController(String fxmlURL) {
        this(fxmlURL, null, null);
    }

    public FXMLWinController(String fxmlURL, String title) {
        this(fxmlURL, null, title);
    }

    public FXMLWinController(T parent) {
        this(null, parent, null);
    }

    public FXMLWinController(T parent, String title) {
        this(null, parent, title);
    }

    public FXMLWinController(String fxmlURL, T parent, String title) {
        if (parent == null) {
            parent = AssetsHelper.<T>loadFXMLView(fxmlURL, this);
        }
        this.setParent(parent);
        this.stage = new Stage();
        this.stage.setScene(new Scene(this.getParent()));
        //注册窗口关闭事件
        this.stage.setOnCloseRequest(this::onRequestClosed);
        this.stage.getIcons().add(AssetsHelper.localIcon("logo.png"));
        if (title != null) {
            this.stage.setTitle(title);
        }
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return this.getStage().getScene();
    }

    public FXMLWinController<T> showAndFront() {
        this.getStage().show();
        this.getStage().toFront();
        return this;
    }

    protected void onRequestClosed(WindowEvent event) {
        this.dispose();
    }

    public void toFixWindow(FXMLWinController controller) {
        //打开新窗口
        controller.showAndFront();
        //关闭当前窗口
        this.getStage().close();
    }

    /**
     *
     * 基于当前窗口打开文件选择器
     *
     */
    public File openFChooser(String title, Path path, FileChooser.ExtensionFilter... filters) {
        var chooser = new FileChooser();
        chooser.setTitle(title);
        if (path != null) {
            chooser.setInitialDirectory(path.toFile());
        }
        chooser.getExtensionFilters().addAll(filters);
        return chooser.showOpenDialog(this.getStage());
    }

    public File openFChooser(String title, FileChooser.ExtensionFilter... filters) {
        return this.openFChooser(title, null, filters);
    }
}
