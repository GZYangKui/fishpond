package cn.navclub.fishpond.app.socket;

import cn.navclub.fishpond.protocol.model.TProMessage;

public interface SocketHook {
    /**
     * 消息到达时候回调该函数
     */
    default void onMessage(TProMessage message){

    }


    /**
     *
     * TCP connection status change callback that function
     *
     */
   default void onTCNStatusChange(TCNStatus oldValue,TCNStatus newValue){

   }
}
