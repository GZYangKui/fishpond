package cn.navclub.fishpond.app.util;

import org.controlsfx.dialog.ExceptionDialog;

public class DialogUtil {
    public static void showEXDialog(Throwable t, String text) {
        var dialog = new ExceptionDialog(t);
        dialog.setTitle("程序错误");
        dialog.setHeaderText(text);
        dialog.setContentText(t.getMessage());
        dialog.showAndWait();
    }
}
