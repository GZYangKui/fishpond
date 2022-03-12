package cn.navclub.fishpond.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;

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

    public static Image localIcon(String file) {
        var url = AssetsHelper.class.getResource("icon/" + file);
        if (url == null) {
            throw new RuntimeException(String.format("本地图标[%s]不存在!"));
        }
        try {
            return new Image(url.openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String loadStyleSheets(String sheets) {
        var url = AssetsHelper.class.getResource("css/" + sheets);
        if (url == null) {
            throw new RuntimeException("Style sheets [" + sheets + "] Not Found!");
        }
        return url.toExternalForm();
    }
}
