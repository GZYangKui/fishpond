package cn.navclub.fishpond.app.socket;

import lombok.Getter;

/***
 *
 *
 * 枚举TCP连接状态
 *
 */
@Getter
public enum TCNStatus {
    /**
     * 待连接
     */
    TO_BE_CONNECTED("未连接"),
    /***
     * 连接中
     */
    CONNECTING("连接中"),
    /***
     * 已连接
     */
    CONNECTED("已连接"),
    /***
     * 已关闭
     */
    CLOSED("已关闭");

    private final String text;

    TCNStatus(String text) {
        this.text = text;
    }
}
