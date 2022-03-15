package cn.navclub.fishpond.server.security;

import cn.navclub.fishpond.server.model.FPSession;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.ext.auth.authorization.AuthorizationContext;
import io.vertx.ext.auth.authorization.impl.AuthorizationContextImpl;

import java.util.Objects;

public class FPUser implements User {
    private final FPSession session;

    public FPUser(FPSession session) {
        this.session = session;
    }

    public Integer getUsername() {
        return this.session.getUsername();
    }

    public String getNickname() {
        return this.session.getNickname();
    }

    public String getAvatar() {
        return this.session.getAvatar();
    }

    public Long getUserId() {
        return this.session.getId();
    }


    @Override
    public JsonObject attributes() {
        return new JsonObject();
    }

    @Override
    public User isAuthorized(Authorization authority, Handler<AsyncResult<Boolean>> resultHandler) {
        Objects.requireNonNull(authority);
        Objects.requireNonNull(resultHandler);

        AuthorizationContext context = new AuthorizationContextImpl(this);
        resultHandler.handle(Future.succeededFuture(authority.match(context)));
        return this;
    }

    @Override
    public JsonObject principal() {
        return Json.encodeToBuffer(this.session).toJsonObject();
    }

    @Override
    public void setAuthProvider(AuthProvider authProvider) {

    }

    @Override
    public User merge(User other) {
        return null;
    }
}
