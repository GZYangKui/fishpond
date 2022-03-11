package cn.navclub.fishpond.server.model;

public class FPSession {
    private Long id;
    private Long expire;
    private Integer username;
    private String  avatar;
    private String  nickname;

    public FPSession(Long id, Long expire, Integer username, String avatar, String nickname) {
        this.id = id;
        this.expire = expire;
        this.username = username;
        this.avatar = avatar;
        this.nickname = nickname;
    }

    public FPSession() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    public Integer getUsername() {
        return username;
    }

    public void setUsername(Integer username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
