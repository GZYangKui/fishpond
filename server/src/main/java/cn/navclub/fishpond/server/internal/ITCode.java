package cn.navclub.fishpond.server.internal;

public enum ITCode {

    UPDATE_SESSION("更新会话");

    private final String title;

    ITCode(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
