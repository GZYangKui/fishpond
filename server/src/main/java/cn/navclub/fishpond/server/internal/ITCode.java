package cn.navclub.fishpond.server.internal;

public enum ITCode {

    CREATE_SESSION("创建会话"),
    CHECK_SESSION("检查会话");

    private final String title;

    ITCode(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
