package cn.navclub.fishpond.protocol.util;

import cn.navclub.fishpond.core.config.Constant;
import cn.navclub.fishpond.core.config.SysProperty;
import cn.navclub.fishpond.core.util.StrUtil;
import cn.navclub.fishpond.protocol.enums.ContentType;
import cn.navclub.fishpond.protocol.enums.MessageT;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.model.TProMessage;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;

import java.security.PublicKey;

public class TProUtil {
    public static Future<Void> feedback(NetSocket socket, TProMessage tPro, JsonObject content) {
        var feedback = new TProMessage();

        //如果消息id字段为空则以0填充
        if (feedback.getMsgId() == null) {
            feedback.setMsgId(0L);
        }

        feedback.setTo(tPro.getFrom());
        feedback.setType(MessageT.JSON);
        feedback.setFrom(SysProperty.SYS_ID);
        feedback.setServiceCode(ServiceCode.OPERATE_FEEDBACK);

        var data = new JsonObject();

        data.put(Constant.UUID, tPro.getMsgId());
        data.put(Constant.SERVICE_CODE, tPro.getServiceCode().getValue());

        if (content != null) {
            data.put(Constant.CONTENT, content);
        }

        feedback.setData(data.toBuffer());

        return socket.write(feedback.toMessage());
    }

    public static ServiceCode getFBCode(TProMessage tPro) {
        if (tPro.getServiceCode() != ServiceCode.OPERATE_FEEDBACK) {
            return ServiceCode.UNKNOWN;
        }
        return ServiceCode.serviceCode(tPro.toJson().getInteger(Constant.SERVICE_CODE));
    }
}
