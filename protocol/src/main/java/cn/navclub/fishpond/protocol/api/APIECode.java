package cn.navclub.fishpond.protocol.api;

public enum APIECode implements IErrorCode {
    OK(200, "处理成功"),
    COMMON_FAIL(300, "处理失败"),
    PARAM_VALID_FAIL(400, "参数校验失败"),
    FORBIDDEN(403,"用户验证失败"),
    SERVER_ERROR(500, "服务器错误");

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
