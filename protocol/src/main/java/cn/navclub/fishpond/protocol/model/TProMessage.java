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
     * 数据
     */
    private Buffer data;
    /**
     * 用户标识
     */
    private String userId;
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
        //用户标识
        buffer.appendBytes(userId.getBytes(StandardCharsets.UTF_8), 0, 32);
        //数据长度
        buffer.appendBytes(BitUtil.int2Byte4(this.data.length()), 2, 2);
        //数据
        buffer.appendBuffer(this.data);
        return buffer;
    }

    public static TProMessage create(byte[] arr, int offset, int dataLen) {
        var pro = new TProMessage();
        //数据类型
        byte[] bytes = {arr[offset + 4], arr[offset + 3], 0, 0};
        pro.type = MessageT.getInstance(BitUtil.byte2Int(bytes));
        //业务代码
        bytes = new byte[]{arr[offset + 6], arr[offset + 5], 0, 0};
        pro.serviceCode = ServiceCode.serviceCode(BitUtil.byte2Int(bytes));
        //用户id
        bytes = new byte[32];
        System.arraycopy(arr, offset + 7, bytes, 0, 32);
        pro.userId = new String(bytes);
        //数据长度
        bytes = new byte[dataLen];
        System.arraycopy(arr, offset + DefaultDecoder.MES_HEADER_LEN, bytes, 0, dataLen);
        //数据内容
        pro.data = Buffer.buffer(bytes);
        return pro;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("\n=======================================================\n");
        sb.append("消息类型:").append(this.getType().getText()).append("\n");
        sb.append("业务代码:").append(this.getServiceCode().getText()).append("\n");
        sb.append("用户标识:").append(this.userId).append("\n");
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
}
