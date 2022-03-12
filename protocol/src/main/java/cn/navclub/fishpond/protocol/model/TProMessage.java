package cn.navclub.fishpond.protocol.model;

import cn.navclub.fishpond.core.util.StrUtil;
import cn.navclub.fishpond.protocol.Protocol;
import cn.navclub.fishpond.protocol.enums.MessageT;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.impl.DefaultDecoder;
import cn.navclub.fishpond.protocol.util.BitUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Data
public class TProMessage extends Protocol {
    /**
     * 消息类型
     */
    private MessageT type;
    /**
     * 消息记录id
     */
    private String uuid;
    /**
     * 数据
     */
    private Buffer data;
    /**
     * 发送消息用户账号
     */
    private Integer from;
    /**
     * 接收消息用户账号
     */
    private Integer to;
    /**
     * 业务代码
     */
    private ServiceCode serviceCode;

    @Override
    public Buffer toMessage() {
        //如果用户未设置消息ID,使用系统UUID
        if (StrUtil.isEmpty(this.getUuid())) {
            this.setUuid(StrUtil.uuid());
        }
        var buffer = Buffer.buffer(DefaultDecoder.FLAGS);
        //消息类型
        buffer.appendBytes(BitUtil.int2Byte4(type.getVal()), 2, 2);
        //业务代码
        buffer.appendBytes(BitUtil.int2Byte4(serviceCode.getValue()), 2, 2);
        //发送消息用户账号
        buffer.appendBytes(BitUtil.int2Byte4(this.getFrom()));
        //接收消息用户账号
        buffer.appendBytes(BitUtil.int2Byte4(this.getTo()));
        //消息记录id
        buffer.appendBytes(uuid.getBytes(), 0, 32);
        //计算当前数据长度
        var length = this.getData() == null ? 0 : this.getData().length();
        //数据长度
        buffer.appendBytes(BitUtil.int2Byte4(length), 2, 2);
        //添加数据内容
        if (length > 0) {
            buffer.appendBuffer(this.getData());
        }
        return buffer;
    }

    public static TProMessage create(byte[] arr, int offset, int dataLen) {
        var msg = new TProMessage();
        //数据类型
        byte[] bytes = {0, 0, arr[offset + 3], arr[offset + 4]};
        msg.type = MessageT.getInstance(BitUtil.byte2Int(bytes));
        //业务代码
        bytes = new byte[]{0, 0, arr[offset + 5], arr[offset + 6]};
        msg.serviceCode = ServiceCode.serviceCode(BitUtil.byte2Int(bytes));
        //发送消息用户账号
        System.arraycopy(arr, offset + 7, bytes, 0, 4);
        msg.from = BitUtil.byte2Int(bytes);
        System.arraycopy(arr, offset + 11, bytes, 0, 4);
        msg.to = BitUtil.byte2Int(bytes);
        bytes = new byte[32];
        //消息记录id
        System.arraycopy(arr, offset + 15, bytes, 0, 32);
        msg.uuid = new String(bytes);
        //数据长度
        bytes = new byte[dataLen];
        System.arraycopy(arr, offset + DefaultDecoder.MES_HEADER_LEN, bytes, 0, dataLen);
        //数据内容
        msg.data = Buffer.buffer(bytes);
        return msg;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("\n=======================================================\n");
        sb.append("消息类型:").append(this.getType().getText()).append("\n");
        sb.append("消息标识:").append(this.getUuid()).append("\n");
        sb.append("业务代码:").append(this.getServiceCode().getText()).append("\n");
        sb.append("FROM:").append(this.getFrom()).append("\n");
        sb.append("TO:").append(this.getTo()).append("\n");
        sb.append("目标数据:").append(this.data.toString(StandardCharsets.UTF_8)).append("\n");
        sb.append("==========================================================\n");
        return sb.toString();
    }

    @Override
    public String getDataStr() {
        if (this.data == null) {
            return "";
        }
        return this.data.toString(StandardCharsets.UTF_8);
    }

    @Override
    public JsonObject toJson() {
        return this.data.toJsonObject();
    }


    @Override
    public JsonArray toJsonArray() {
        return this.data.toJsonArray();
    }
}
