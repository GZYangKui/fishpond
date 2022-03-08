package cn.navclub.fishpond.protocol.enums;

/**
 * 枚举系统业务代码
 */
public enum ServiceCode {
    UNKNOWN(-2 >> 1, "未知业务代码"),
    HEART_BEAT(0, "心跳"),
    SEND(2 >>> 1, "发送消息"),
    SYSTEM_MSG(4 >>> 1, "系统消息");

    private final int value;
    private final String text;

    ServiceCode(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static ServiceCode serviceCode(int val) {
        for (ServiceCode value : values()) {
            if (value.value == val) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
