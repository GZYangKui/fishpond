package cn.navclub.fishpond.app.socket;

/***
 *
 *
 * 枚举TCP连接状态
 *
 */
public enum TCNStatus {
    /**
     * 待连接
     */
    TO_BE_CONNECTED,
    /***
     * 连接中
     */
    CONNECTING,
    /***
     * 已连接
     */
    CONNECTED,
    /***
     * 已关闭
     */
    CLOSED
}
