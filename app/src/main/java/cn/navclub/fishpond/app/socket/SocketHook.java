package cn.navclub.fishpond.app.socket;

import cn.navclub.fishpond.protocol.api.APIECode;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.model.TProMessage;
import io.vertx.core.json.JsonObject;

public interface SocketHook {
    /**
     * 消息到达时候回调该函数
     */
    default void onMessage(TProMessage message) {

    }

    /**
     * 当操作反馈消息到达时出发该函数
     */
    default void feedback(ServiceCode serviceCode, APIECode code, JsonObject content, TProMessage message) {

    }


    /**
     * TCP connection status change callback that function
     */
    default void onTCNStatusChange(TCNStatus oldValue, TCNStatus newValue) {

    }
}
