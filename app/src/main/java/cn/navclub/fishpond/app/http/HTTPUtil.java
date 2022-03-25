package cn.navclub.fishpond.app.http;

import cn.navclub.fishpond.app.Main;
import cn.navclub.fishpond.core.config.Constant;
import cn.navclub.fishpond.protocol.api.APIECode;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import javafx.application.Platform;
import javafx.geometry.Pos;
import lombok.Getter;
import lombok.Setter;
import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.ExceptionDialog;

import java.util.Map;
import java.util.Optional;

import static cn.navclub.fishpond.core.config.Constant.*;

public class HTTPUtil {
    @Getter
    private static final String HOST = "127.0.0.1";
    @Getter
    private static final int PORT = 10000;

    @Getter
    @Setter
    private volatile static int username;

    @Getter
    @Setter
    private volatile static String sessionId;


    public static <T> Future<T> doGet(API api, Map<String, String> queries) {
        return create(true, api, queries, null);
    }

    public static <T> Future<T> doPost(API api, Map<String, String> queries, JsonObject data) {
        return create(false, api, queries, data);
    }

    private static <T> Future<T> create(boolean get, API api, Map<String, String> queries, JsonObject data) {
        var client = WebClient.create(Main.vertx);
        HttpRequest<Buffer> request;
        if (get) {
            request = client.get(PORT, HOST, api.getUrl());
        } else {
            request = client.post(PORT, HOST, api.getUrl());
        }
        request.putHeader(Constant.SESSION_ID, Optional.ofNullable(sessionId).orElse(""));
        if (queries != null) {
            for (Map.Entry<String, String> entry : queries.entrySet()) {
                request.addQueryParam(entry.getKey(), entry.getValue());
            }
        }
        final Future<HttpResponse<Buffer>> future;
        if (get) {
            future = request.send();
        } else {
            future = request.sendJson(data);
        }
        var promise = Promise.<T>promise();
        future.onComplete(ar -> {
            Platform.runLater(() -> {
                if (ar.succeeded()) {
                    var json = ar.result().bodyAsJsonObject();
                    var code = json.getInteger(CODE);
                    var success = code == APIECode.OK.getCode();
                    if (success) {
                        promise.complete((T) (json.getValue(DATA)));
                    } else {
                        promise.fail(code.toString());
                        prompt(json.getString(MESSAGE));
                    }
                } else {
                    //对话框提示网络错误,不通知前端程序
                    requestError(ar.cause(), api);
                }
            });
        });
        return promise.future();
    }

    private static void prompt(String text) {
        Notifications
                .create()
                .position(Pos.TOP_RIGHT)
                .text(text)
                .showWarning();
    }

    /**
     * 显示网络请求错误对话框
     */
    private static void requestError(Throwable t, API api) {
        var exPane = new ExceptionDialog(t);
        exPane.setTitle("网络请求错误");
        exPane.setHeaderText(t.getMessage());
        exPane.setContentText(api.getText() + "[" + api.getUrl() + "]");
        exPane.showAndWait();
    }
}
