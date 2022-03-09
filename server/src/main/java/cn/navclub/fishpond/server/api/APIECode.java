package cn.navclub.fishpond.server.api;

public enum APIECode implements IErrorCode {
    OK(200, "处理成功"),
    PARAM_VALID_FAIL(400,"参数校验失败");

    private final int code;
    private final String message;

    APIECode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
    }