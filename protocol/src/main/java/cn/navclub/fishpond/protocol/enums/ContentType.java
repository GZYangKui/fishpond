package cn.navclub.fishpond.protocol.enums;

import lombok.Getter;

/**
 * 定义消息内容
 */
@Getter
public enum ContentType {
    UN_SUPPORT("未支持消息内容", -1),
    PLAIN_TEXT("文本", 0),
    IMG("图片", 1);

    private final int value;
    private final String text;

    ContentType(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public static ContentType getInstance(int value) {
        for (ContentType contentType : values()) {
            if (contentType.value == value) {
                return contentType;
            }
        }
        return UN_SUPPORT;
    }
}
