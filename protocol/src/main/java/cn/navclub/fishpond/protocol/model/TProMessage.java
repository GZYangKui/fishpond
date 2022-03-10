package cn.navclub.fishpond.protocol.model;

import cn.navclub.fishpond.protocol.Protocol;
import cn.navclub.fishpond.protocol.enums.MessageT;
import cn.navclub.fishpond.protocol.enums.ServiceCode;
import cn.navclub.fishpond.protocol.impl.DefaultDecoder;
import cn.navclub.fishpond.protocol.util.BitUtil;
import io.vertx.core.buffer.Buffer;

import java.nio.charset.StandardCharsets;


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
     * 用户标识(该字段属于保留字段)
     */
    private String userId;
    /**
     * 目标用户id
     */
    private String targetId;
    /**
     * 业务代码
     */
    private ServiceCode serviceCode;

    @Override
    public Buffer toMessage() {
        var buffer = Buffer.buffer(DefaultDecoder.FLAGS);
        //消息类型
        buffer.appendBytes(BitUtil.int2Byte4(type.getVal()), 2, 2);
        //业务代码
        buffer.appendBytes(BitUtil.int2Byte4(serviceCode.getValue()), 2, 2);
        //目标用户
        buffer.appendBytes(targetId.getBytes(), 0, 32);
        //消息记录id
        buffer.appendBytes(uuid.getBytes(), 0, 32);
        //数据长度
        buffer.appendBytes(BitUtil.int2Byte4(this.data.length()), 2, 2);
        //数据
        buffer.appendBuffer(this.data);
        return buffer;
    }

    public static TProMessage create(byte[] arr, int offset, int dataLen) {
        var msg = new TProMessage();
        //数据类型
        byte[] bytes = {arr[offset + 4], arr[offset + 3], 0, 0};
        msg.type = MessageT.getInstance(BitUtil.byte2Int(bytes));
        //业务代码
        bytes = new byte[]{arr[offset + 6], arr[offset + 5], 0, 0};
        msg.serviceCode = ServiceCode.serviceCode(BitUtil.byte2Int(bytes));
        bytes = new byte[32];
        //目标用户id
        System.arraycopy(arr, offset + 7, bytes, 0, 32);
        msg.targetId = new String(bytes);
        //消息记录id
        System.arraycopy(arr, offset + 39, bytes, 0, 32);
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
//        sb.append("用户标识:").append(this.getUserId()).append("\n");
        sb.append("目标用户:").append(this.getTargetId()).append("\n");
        sb.append("目标数据:").append(this.data.toString(StandardCharsets.UTF_8)).append("\n");
        sb.append("==========================================================\n");
        return sb.toString();
    }

    public void setType(MessageT type) {
        this.type = type;
    }

    public MessageT getType() {
        return type;
    }

    @Override
    public String getDataStr() {
        if (this.data == null) {
            return "";
        }
        return this.data.toString(StandardCharsets.UTF_8);
    }

    public Buffer getData() {
        return data;
    }

    public void setData(Buffer data) {
        this.data = data;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setServiceCode(ServiceCode serviceCode) {
        this.serviceCode = serviceCode;
    }

    public ServiceCode getServiceCode() {
        return serviceCode;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
