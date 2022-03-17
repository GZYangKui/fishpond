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

        feedback.setTo(tPro.getFrom());
        feedback.setType(MessageT.JSON);
        feedback.setUuid(StrUtil.uuid());
        feedback.setFrom(SysProperty.SYS_ID);
        feedback.setServiceCode(ServiceCode.OPERATE_FEEDBACK);

        var data = new JsonObject();

        data.put(Constant.UUID, tPro.getUuid());
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

    public static TProMessage hello() {
        var tPro = new TProMessage();


        tPro.setType(MessageT.JSON);
        tPro.setUuid(StrUtil.uuid());
        tPro.setTo(SysProperty.SYS_ID);
        tPro.setFrom(SysProperty.SYS_ID);
        tPro.setServiceCode(ServiceCode.GROUP_MESSAGE);

        var item = new JsonObject();

        item.put(Constant.MESSAGE, "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01c4445d6dd08ea80120526ddadf94.gif&refer=http%3A%2F%2Fimg.zcool.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1650089694&t=2aa8d667ba7d524c6067d2f5bdd042d3");
        item.put(Constant.TYPE, ContentType.IMG.getValue());

        var item1 = new JsonObject();
        item1.put(Constant.MESSAGE, "测试图片图片发布\n");
        item1.put(Constant.TYPE, ContentType.PLAIN_TEXT.getValue());

        var json = new JsonObject()
                .put(Constant.TIMESTAMP, System.currentTimeMillis())
                .put(Constant.ITEMS, new JsonArray().add(item1).add(item));

        tPro.setData(json.toBuffer());

        return tPro;
    }
}
