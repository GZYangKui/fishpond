package cn.navclub.fishpond.app.controls;

import cn.navclub.fishpond.app.AbstractController;
import cn.navclub.fishpond.app.AssetsHelper;
import cn.navclub.fishpond.app.controller.FPController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class XWinControl extends AbstractController<HBox> implements EventHandler<MouseEvent> {
    private double xOffset = 0;
    private double yOffset = 0;

    private XWinControl() {
        this.setParent(AssetsHelper.loadFXMLView("XWin.fxml", this));
        //注册鼠标事件
        this.getParent().addEventFilter(MouseEvent.ANY, this);
    }

    public static XWinControl create() {
        return new XWinControl();
    }

    @FXML
    public void min() {
        ((Stage) this.window()).setIconified(true);
    }

    @FXML
    public void close() {
        ((Stage) this.window()).close();
    }

    @Override
    public void handle(MouseEvent event) {

        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {

            xOffset = event.getSceneX();
            yOffset = event.getSceneY();

        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {

            FPController.getController().getStage().setX(event.getScreenX() - xOffset);

            if (event.getScreenY() - yOffset < 0) {
                FPController.getController().getStage().setY(0);
            } else {
                FPController.getController().getStage().setY(event.getScreenY() - yOffset);
            }

        }
    }
}
