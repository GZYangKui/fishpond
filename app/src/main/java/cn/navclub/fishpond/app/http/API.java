package cn.navclub.fishpond.app.http;


public enum API {
    UPLOAD_FILE("/api/file/upload", "文件上传"),
    REQ_LOGIN("/api/user/login", "请求登录");

    private final String url;
    private final String text;

    API(String url, String text) {
        this.url = url;
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public String getText() {
        return text;
    }
}
