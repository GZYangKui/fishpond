package cn.navclub.fishpond.protocol.enums;

/**
 * 枚举系统业务代码
 */
public enum ServiceCode {
    UNKNOWN(-2 >> 1, "未知业务代码"),
    HEART_BEAT(0, "心跳"),
    SEND(2 >>> 1, "发送消息"),
    TCP_REGISTER(6 >>> 1, "连接注册"),
    SYSTEM_NOTIFY(4 >>> 1, "系统通知"),
    OPERATE_FEEDBACK(10, "操作反馈");

    private final int value;
    private final String text;
    //是否系统行为
    private final boolean system;

    ServiceCode(int value, String text) {
        this.system = false;
        this.value = value;
        this.text = text;
    }

    ServiceCode(int value, String text, boolean system) {
        this.value = value;
        this.text = text;
        this.system = system;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public boolean isSystem() {
        return system;
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
