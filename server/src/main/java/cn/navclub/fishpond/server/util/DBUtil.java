package cn.navclub.fishpond.server.util;

import cn.navclub.fishpond.core.config.Constant;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.impl.SqlClientInternal;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cn.navclub.fishpond.core.config.Constant.DATABASE;


public class DBUtil {
    private static MySQLPool client;
    private static volatile boolean initDatabase;

    public synchronized static Future<Void> createSharedDatabase(Vertx vertx, JsonObject dataSource) {
        if (initDatabase) {
            throw new RuntimeException("Database already init please don't repeat init.");
        }
        DBUtil.initDatabase = true;
        var options = new MySQLConnectOptions();

        options.setDatabase(dataSource.getString(DATABASE));
        options.setUser(dataSource.getString(Constant.USERNAME));
        options.setCharset(dataSource.getString(Constant.CHARSET));
        options.setPassword(dataSource.getString(Constant.PASSWORD));


        var pool = dataSource.getJsonObject(Constant.POOL);

        var pOptions = new PoolOptions();

        pOptions.setName(pool.getString(Constant.NAME));
        pOptions.setShared(pool.getBoolean(Constant.SHARE));
        pOptions.setMaxSize(pool.getInteger(Constant.MAX_SIZE));

        client = MySQLPool.pool(vertx, options, pOptions);

        var future = client.query("SELECT  1").execute();
        var promise = Promise.<Void>promise();
        future.onComplete(ar -> {
            if (ar.failed()) {
                initDatabase = false;
                promise.fail(ar.cause());
            } else {
                promise.complete();
            }
        });
        return promise.future();
    }

    public static <T> Future<Optional<T>> findOne(RowMapper<T> mapper, String sql, Map<String, Object> params) {
        var future = forQuery(mapper, sql, params);
        var promise = Promise.<Optional<T>>promise();
        future.onComplete(ar -> {
            if (ar.failed()) {
                promise.fail(ar.cause());
            } else {
                var list = ar.result();
                promise.complete(Optional.ofNullable(list.size() > 0 ? list.get(0) : null));
            }
        });
        return promise.future();
    }

    public static <T> Future<List<T>> forQuery(RowMapper<T> mapper, String sql, Map<String, Object> params) {
        var future = SqlTemplate
                .forQuery(getClient(), sql)
                .mapTo(mapper)
                .execute(params);
        var promise = Promise.<List<T>>promise();
        future.onComplete(ar -> {
            if (ar.failed()) {
                promise.fail(ar.cause());
            } else {
                var list = new ArrayList<T>();
                for (T t : ar.result()) {
                    list.add(t);
                }
                promise.complete(list);
            }
        });
        return promise.future();
    }

    public static SqlClient getClient() {
        return client;
    }
}
