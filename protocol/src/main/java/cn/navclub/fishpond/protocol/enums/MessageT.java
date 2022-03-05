package cn.navclub.fishpond.protocol.enums;

/**
 * 枚举当前支持数据类型
 */
public enum MessageT {
    /**
     * 未知数据类型
     */
    UNKNOWN(-2 << 1, "未知类型"),
    /**
     * 普通文字
     */
    TEXT(0, "普通文本"),
    /**
     * json数据
     */
    JSON(2 >> 1, "JSON数据"),
    /**
     * 二进制数据
     */
    BINARY(4 >> 1, "二进制数据");

    private final Integer val;
    private final String text;

    MessageT(Integer val, String text) {
        this.val = val;
        this.text = text;
    }

    public Integer getVal() {
        return val;
    }

    public String getText() {
        return text;
    }

    public static MessageT getInstance(int val) {
        for (MessageT value : values()) {
            if (val == value.val) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
