# 鱼塘(fishpond)

> 一款上班摸鱼神器,咳咳,其实我们是正儿八经的即时聊天程序

# 通讯协议

字节| 1-3 | 4-5 | 6-7 | 8-11     | 12-43 | 44-45 | 46.... |
---|-----|-----|-------|----------|-------|-------|--------|
内容| TNB | 消息类型| 业务代码  | 用户账号(接收) | 消息ID  | 数据长度  | 数据内容   |

* TNB为消息固定标识
* 消息类型,目前仅支持普通文本、json数据、二进制数据,后期可能会有其他扩展，具体请查看[数据类型](./protocol/src/main/java/cn/navclub/fishpond/protocol/enums/MessageT.java)
* 用户标识,用户登录后系统分配给用户一个32字节唯一字符串作为用户唯一标识，系统消息该字段以32字节0填充。
* 数据长度,占用2个字节，也就是说当前协议最多允许传递64kb数据,大于该数据长度将无法传递。
* 数据内容,具体传输数据