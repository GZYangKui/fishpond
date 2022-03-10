package cn.navclub.fishpond.server.internal;

import cn.navclub.fishpond.protocol.api.APIECode;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import java.util.Optional;

public class ITResult<T> {
    private T data;
    private Integer code;
    private String message;

    public ITResult() {
    }

    public ITResult(T data, APIECode sysECode, String message) {
        this.data = data;
        this.code = sysECode.getCode();
        this.message = Optional.ofNullable(message).orElse(sysECode.message());
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }


    public final JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }

    public final Buffer toBuffer() {
        var json = this.toJson();
        json.remove("sysECode");
        return json.toBuffer();
    }

    public static <T> ITResult<T> success(String message) {
        return new ITResult<>(null, APIECode.OK, message);
    }

    public static <T> ITResult<T> success(T data) {
        return new ITResult<>(data, APIECode.OK, null);
    }

    public static <T> ITResult<T> fail(String message) {
        return new ITResult<>(null, APIECode.COMMON_FAIL, message);
    }
}
