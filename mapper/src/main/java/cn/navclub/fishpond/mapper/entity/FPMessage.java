package cn.navclub.fishpond.mapper.entity;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.sqlclient.templates.annotations.Column;
import io.vertx.sqlclient.templates.annotations.RowMapped;

import java.time.LocalDateTime;

@DataObject
@RowMapped
public class FPMessage {
    /**
     * 消息记录id
     */
    private Long id;
    /**
     * 消息数据类型
     */
    private Integer type;
    /**
     * 消息发送者
     */
    private Integer sender;
    /**
     * 消息接收者
     */
    private Integer receiver;
    /**
     * 消息内容
     */
    private String message;
    /**
     * 业务码
     */
    @Column(name = "s_code")
    private Integer sCode;

    @Column(name = "create_time")
    private LocalDateTime createTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSender() {
        return sender;
    }

    public void setSender(Integer sender) {
        this.sender = sender;
    }

    public Integer getReceiver() {
        return receiver;
    }

    public void setReceiver(Integer receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getsCode() {
        return sCode;
    }

    public void setsCode(Integer sCode) {
        this.sCode = sCode;
    }
}
