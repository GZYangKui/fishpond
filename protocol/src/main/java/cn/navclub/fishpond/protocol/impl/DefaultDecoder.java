package cn.navclub.fishpond.protocol.impl;

import cn.navclub.fishpond.protocol.Decoder;
import cn.navclub.fishpond.protocol.model.TProMessage;
import cn.navclub.fishpond.protocol.util.BitUtil;
import io.vertx.core.buffer.Buffer;


/**
 * 默认解码器实现
 * <table>
 *     <tr>
 *          <th>字节</th>
 *          <th>1-3</th>
 *          <th>4-5</th>
 *          <th>6-7</th>
 *          <th>8-39</th>
 *          <th>40-71</th>
 *          <th>72-73</th>
 *          <th>74......</th>
 *     </tr>
 *     <tr>
 *         <td>内容</td>
 *         <td>TNB</td>
 *         <td>消息类型</td>
 *         <td>业务代码</td>
 *         <td>用户标识(接收)</td>
 *         <th>消息ID</th>
 *         <td>消息长度</td>
 *         <td>消息内容</td>
 *     </tr>
 *
 * </table>
 */
public class DefaultDecoder extends Decoder<TProMessage> {
    /**
     * 消息头长度
     */
    public static final int MES_HEADER_LEN = 73;
    /**
     * 消息标志位(NBT)
     */
    public static final byte[] FLAGS = {0x4E, 0x54, 0x42};

    private Buffer buffer;

    private boolean parsing;

    public DefaultDecoder() {
        parsing = false;
    }

    @Override
    public void handle(Buffer event) {
        if (this.parsing) {
            return;
        }
        if (buffer == null) {
            buffer = Buffer.buffer();
        }
        buffer.appendBuffer(event);
        this.parsing = true;
        try {
            this.handleParse();
        } catch (Exception e) {
            if (this.exHandler != null) {
                this.exHandler.handle(e);
            }
        } finally {
            this.parsing = false;
        }
    }

    private void handleParse() {
        do {
            //检测消息头是否完整
            if (this.buffer.length() < MES_HEADER_LEN) {
                return;
            }
            var bytes = this.buffer.getBytes();
            var len = bytes.length;
            var offset = this.getFlagPos(bytes, len);
            var hEnd = offset + MES_HEADER_LEN;
            var full = false;
            if ((full = offset == -1) || hEnd >= len) {
                //消息头不完整,丢弃该段消息
                if (full) {
                    this.buffer = Buffer.buffer();
                }
                return;
            }
            var dataLen = this.getDataSize(bytes, offset);
            var endPos = offset + dataLen + MES_HEADER_LEN;
            if (endPos > len) {
                return;
            }
            var pro = TProMessage.create(bytes, offset, dataLen);
            if (this.handler != null) {
                this.handler.handle(pro);
            }
            this.buffer = this.buffer.getBuffer(endPos, len);
        } while (true);
    }

    /**
     * 查询数据包头标识位
     */
    private int getFlagPos(byte[] bytes, int len) {
        for (int i = 0; i < len; i++) {
            if (i + 3 >= len) {
                break;
            }
            var f1 = bytes[i];
            var f2 = bytes[i + 1];
            var f3 = bytes[i + 2];
            if ((f1 | f2 | f3) == (FLAGS[0] | FLAGS[1] | FLAGS[2])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 从消息头中获取数据长度信息
     */
    private int getDataSize(byte[] bytes, int offset) {
        var arr = new byte[4];
        var pos = offset + MES_HEADER_LEN - 2;
        arr[1] = bytes[pos];
        arr[0] = bytes[pos + 1];
        return BitUtil.byte2Int(arr);
    }

    public static Decoder<TProMessage> create() {
        return new DefaultDecoder();
    }
}
