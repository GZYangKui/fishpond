package cn.navclub.fishpond.server.util;

import cn.navclub.fishpond.core.config.Constant;
import cn.navclub.fishpond.core.util.StrUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisClientType;
import io.vertx.redis.client.RedisOptions;

public class RedisUtil {

    private static Redis redis;

    private static boolean init;

    public static synchronized Future<Void> createRedisClient(Vertx vertx, JsonObject config) {
        if (init) {
            return Future.failedFuture("Redis组件已初始化/正在初始化中,请稍候再试!");
        }
        init = true;

        var host = config.getString(Constant.HOST);
        var port = config.getInteger(Constant.PORT);
        var poolSize = config.getInteger(Constant.MAX_SIZE);
        var password = config.getString(Constant.PASSWORD);

        var options = new RedisOptions();
        options.setMaxPoolSize(poolSize);
        options.setType(RedisClientType.STANDALONE);
        options.setPassword(StrUtil.isEmpty(password) ? null : password);
        options.setConnectionString(String.format("redis://%s:%d", host, port));

        redis = Redis.createClient(vertx, options);

        var promise = Promise.<Void>promise();

        redis.connect().onComplete(ar -> {
            if (ar.succeeded()) {
                promise.complete();
                ar.result().close();
            } else {
                init = false;
                promise.fail(ar.cause());
            }
        });

        return promise.future();
    }

    public static RedisAPI redisAPI() {
        return RedisAPI.api(getRedis());
    }

    public static Redis getRedis() {
        return redis;
    }
}
