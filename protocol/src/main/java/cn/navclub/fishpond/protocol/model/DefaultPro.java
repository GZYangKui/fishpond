package cn.navclub.fishpond.protocol.model;

import cn.navclub.fishpond.protocol.Protocol;
import cn.navclub.fishpond.protocol.enums.MessageT;
import cn.navclub.fishpond.protocol.impl.DefaultDecoder;
import cn.navclub.fishpond.protocol.util.BitUtil;
import io.vertx.core.buffer.Buffer;

import java.nio.charset.StandardCharsets;


public class DefaultPro extends Protocol {
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

    @Override
    public Buffer toMessage() {
        var buffer = Buffer.buffer(DefaultDecoder.FLAGS);
        buffer.appendBytes(BitUtil.int2Byte4(type.getVal()), 2, 2);
        buffer.appendBytes(userId.getBytes(StandardCharsets.UTF_8));
        buffer.appendBytes(BitUtil.int2Byte4(this.data.length()), 2, 2);
        buffer.appendBuffer(this.data);
        return buffer;
    }

    public static DefaultPro create(byte[] arr, int offset, int dataLen) {
        var pro = new DefaultPro();
        byte[] bytes = {arr[offset + 4], arr[offset + 3], 0, 0};
        pro.type = MessageT.getInstance(BitUtil.byte2Int(bytes));
        bytes = new byte[32];
        System.arraycopy(arr, offset + 5, bytes, 0, 32);
        pro.userId = new String(bytes);
        bytes = new byte[dataLen];
        System.arraycopy(arr, offset + DefaultDecoder.MES_HEADER_LEN, bytes, 0, dataLen);
        pro.data = Buffer.buffer(bytes);
        return pro;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("\n=======================================================\n");
        sb.append("消息类型:").append(this.getType().getText()).append("\n");
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
}
