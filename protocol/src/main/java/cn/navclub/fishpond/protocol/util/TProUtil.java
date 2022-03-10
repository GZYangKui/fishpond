package cn.navclub.fishpond.protocol.util;

import cn.navclub.fishpond.protocol.enums.MessageT;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.model.TProMessage;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;

public class TProUtil {
    public static Future<Void> feedback(NetSocket socket, TProMessage msg, JsonObject content) {
        var feedback = new TProMessage();

        feedback.setType(MessageT.JSON);
        feedback.setUuid(msg.getUuid());
        feedback.setData(content.toBuffer());
        feedback.setTargetId(msg.getTargetId());
        feedback.setServiceCode(ServiceCode.OPERATE_FEEDBACK);

        return socket.write(feedback.toMessage());
    }
}
