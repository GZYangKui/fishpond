package cn.navclub.fishpond.server.util;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
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


public class DBUtil {
    private static MySQLPool client;
    private static volatile boolean initDatabase;

    public synchronized static Future<Void> createSharedDatabase(Vertx vertx, MySQLConnectOptions options, PoolOptions poolOptions) {
        if (initDatabase) {
            throw new RuntimeException("Database already init please don't repeat init.");
        }
        DBUtil.initDatabase = true;
        client = MySQLPool.pool(vertx, options, poolOptions);
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
