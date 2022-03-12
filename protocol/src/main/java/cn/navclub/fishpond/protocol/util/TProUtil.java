package cn.navclub.fishpond.protocol.util;

import cn.navclub.fishpond.core.config.Constant;
import cn.navclub.fishpond.core.config.SysProperty;
import cn.navclub.fishpond.core.util.StrUtil;
import cn.navclub.fishpond.protocol.enums.MessageT;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.model.TProMessage;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;

public class TProUtil {
    public static Future<Void> feedback(NetSocket socket, TProMessage tPro, JsonObject content) {
        var feedback = new TProMessage();

        feedback.setTo(tPro.getFrom());
        feedback.setType(MessageT.JSON);
        feedback.setUuid(StrUtil.uuid());
        feedback.setFrom(SysProperty.SYS_ID);
        feedback.setServiceCode(ServiceCode.OPERATE_FEEDBACK);

        var data = new JsonObject();
        if (content != null) {
            data.put(Constant.CONTENT, content);
        }
        data.put(Constant.UUID, tPro.getUuid());
        data.put(Constant.SERVICE_CODE, tPro.getServiceCode().getValue());

        feedback.setData(data.toBuffer());

        return socket.write(feedback.toMessage());
    }
}
