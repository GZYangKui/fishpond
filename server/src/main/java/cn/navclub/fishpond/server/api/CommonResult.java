package cn.navclub.fishpond.server.api;

import io.vertx.core.json.JsonObject;

import static cn.navclub.fishpond.server.api.APIECode.COMMON_FAIL;

public class CommonResult<T> {
    private Integer code;
    private String message;
    private T data;

    public CommonResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public CommonResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(APIECode.OK.getCode(), APIECode.OK.message(), data);
    }

    public static <T> CommonResult<T> fail(IErrorCode errorCode) {
        return new CommonResult<>(errorCode.getCode(), errorCode.message());
    }

    public static <T> CommonResult<T> fail(IErrorCode errorCode, String message) {
        return new CommonResult<>(errorCode.getCode(), message);
    }

    public static <T> CommonResult<T> fail(String message) {
        return new CommonResult<>(COMMON_FAIL.getCode(), message);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
