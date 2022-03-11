package cn.navclub.fishpond.protocol.api;

import lombok.Data;


@Data
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
        return new CommonResult<>(APIECode.COMMON_FAIL.getCode(), message);
    }
}
