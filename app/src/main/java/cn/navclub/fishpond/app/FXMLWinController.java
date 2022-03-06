package cn.navclub.fishpond.app;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
}
