package cn.navclub.fishpond.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class AssetsHelper {
    public static <T extends Parent> T loadFXMLView(String fxmlView, Object controller) {
        assert fxmlView != null;
        try {
            var fxmlLoader = new FXMLLoader();
            var url = AssetsHelper.class.getResource("fxml/" + fxmlView);
            fxmlLoader.setLocation(url);
            fxmlLoader.setController(controller);
            return fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Load fxml view happen error:" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
