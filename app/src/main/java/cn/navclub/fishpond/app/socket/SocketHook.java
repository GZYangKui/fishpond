package cn.navclub.fishpond.app.socket;

import cn.navclub.fishpond.protocol.model.TProMessage;

public interface SocketHook {
    /**
     * 消息到达时候回调该函数
     */
    void onMessage(TProMessage message);
}
