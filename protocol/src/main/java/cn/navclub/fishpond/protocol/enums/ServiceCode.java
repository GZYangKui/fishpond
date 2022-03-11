package cn.navclub.fishpond.protocol.enums;

/**
 * 枚举系统业务代码
 */
public enum ServiceCode {
    UNKNOWN(-1, "未知业务代码"),
    HEART_BEAT(0, "心跳"),
    SEND(1, "发送消息"),
    TCP_REGISTER(3, "连接注册", true),
    SYSTEM_NOTIFY(2, "系统通知"),
    OPERATE_FEEDBACK(4, "操作反馈"),
    GROUP_MESSAGE(5, "群发消息"),
    P2P_MESSAGE(6, "点对点消息");

    private final int value;
    private final String text;
    //是否允许跳过会话检查
    private final boolean ssCheck;


    ServiceCode(int value, String text, boolean ssCheck) {
        this.text = text;
        this.value = value;
        this.ssCheck = ssCheck;
    }

    ServiceCode(int value, String text) {
        this.value = value;
        this.text = text;
        this.ssCheck = false;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public boolean isSsCheck() {
        return ssCheck;
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
