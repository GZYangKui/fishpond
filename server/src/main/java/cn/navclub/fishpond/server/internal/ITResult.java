package cn.navclub.fishpond.server.internal;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

public class ITResult<T> {
    private T data;
    private Boolean success;
    private String message;

    public ITResult() {
    }

    public ITResult(T data, Boolean success, String message) {
        this.data = data;
        this.success = success;
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public final JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }

    public final Buffer toBuffer() {
        return this.toJson().toBuffer();
    }

    public static <T> ITResult<T> success(String message) {
        return new ITResult<>(null, true, message);
    }

    public static <T> ITResult<T> success(T data) {
        return new ITResult<>(data, true, "操作成功");
    }

    public static <T> ITResult<T> fail(String message) {
        return new ITResult<>(null, false, message);
    }
}
