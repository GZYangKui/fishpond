package cn.navclub.fishpond.app;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Window controller
 *
 * @param <T>
 */
public class FXMLWinController<T extends Parent> extends AbstractController<T> {
    private final Stage stage;

    public FXMLWinController(String fxmlURL) {
        var parent = AssetsHelper.<T>loadFXMLView(fxmlURL, this);
        this.setParent(parent);
        this.setParent(parent);
        this.stage = new Stage();
        this.stage.setScene(new Scene(parent));
        //注册窗口关闭事件
        this.stage.setOnCloseRequest(this::onRequestClosed);
    }

    public FXMLWinController(String fxmlURL, String title) {
        this(fxmlURL);
        this.getStage().setTitle(title);
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
}
