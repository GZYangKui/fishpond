package cn.navclub.fishpond.server.internal;

public enum ITCode {
    CREATE_SESSION("创建会话"),
    CHECK_SESSION("检查会话"),
    SAVE_MESSAGE("消息持久化"),
    REMOVE_TCP_SESSION("移出TCP连接");

    private final String title;

    ITCode(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
