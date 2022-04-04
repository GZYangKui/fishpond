package cn.navclub.fishpond.app.util;

import cn.navclub.fishpond.app.model.UPFileInfo;
import cn.navclub.fishpond.app.socket.SocketHolder;
import cn.navclub.fishpond.core.config.Constant;
import cn.navclub.fishpond.core.config.SysProperty;
import cn.navclub.fishpond.core.util.StrUtil;
import cn.navclub.fishpond.core.worker.SnowFlakeWorker;
import cn.navclub.fishpond.protocol.enums.ContentType;
import cn.navclub.fishpond.protocol.enums.MessageT;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.model.TProMessage;
import io.vertx.core.Future;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static cn.navclub.fishpond.core.config.Constant.*;
import static cn.navclub.fishpond.protocol.enums.MessageT.JSON;

public class TProUtil {
    /**
     * 发送普通文本
     *
     * @param account 接受消息帐号
     * @param text    待发送普通文本
     */
    public static Future<Long> sendPlainText(Integer account, String text) {
        var tPro = create(account);
        var data = new JsonObject();
        data.put(Constant.TIMESTAMP, System.currentTimeMillis());
        data.put(Constant.ITEMS, new JsonArray()
                .add(new JsonObject()
                        .put(Constant.TYPE, ContentType.PLAIN_TEXT.getValue())
                        .put(MESSAGE, text))
        );
        tPro.setData(data.toBuffer());

        //写入消息
        return SocketHolder.getInstance().write(tPro);
    }

    /**
     * 发送文件
     */
    public static Future<Long> sendFile(Integer account, List<String> files) {
        var tPro = create(account);
        var json = new JsonObject();
        json.put(Constant.TIMESTAMP, System.currentTimeMillis());
        var items = new JsonArray();
        for (String file : files) {
            final ContentType type;
            var picture = StrUtil.isPicture(file);
            if (picture) {
                type = ContentType.IMG;
            } else {
                type = ContentType.FILE;
            }
            var item = new JsonObject();

            item.put(MESSAGE, file);
            item.put(TYPE, type.getValue());

            items.add(item);
        }
        json.put(ITEMS, items);
        tPro.setData(json.toBuffer());
        return SocketHolder.getInstance().write(tPro);
    }

    public static Future<Long> sendImage(Integer to, List<UPFileInfo> pictures) {
        var json = new JsonObject();
        var tPro = create(to);
        var message = new JsonObject();

        json.put(ITEMS, new JsonArray().add(message));
        json.put(Constant.TYPE, MessageT.JSON.getVal());
        json.put(Constant.TIMESTAMP, System.currentTimeMillis());

        message.put(Constant.TYPE, ContentType.IMG.getValue());
        for (UPFileInfo picture : pictures) {
            var data = new JsonObject();

            data.put(URL, picture.getUrl());
            data.put(FILENAME, picture.getFilename());
            data.put(FILESIZE, picture.getFileSize());
            data.put(WIDTH, picture.getImageInfo().getWidth());
            data.put(HEIGHT, picture.getImageInfo().getHeight());

            message.put(picture.thumbnail() ? THUMBNAIL : IMAGE, data);
        }


        tPro.setData(json.toBuffer());

        return SocketHolder.getInstance().write(tPro);
    }

    private static TProMessage create(Integer to) {
        var tPro = new TProMessage();

        tPro.setTo(to);
        tPro.setType(JSON);
        tPro.setFrom(SysProperty.SYS_ID);
        //0代表系统群发,否则属于点对点通信
        tPro.setServiceCode(to == 0 ? ServiceCode.GROUP_MESSAGE : ServiceCode.P2P_MESSAGE);

        return tPro;
    }
}
