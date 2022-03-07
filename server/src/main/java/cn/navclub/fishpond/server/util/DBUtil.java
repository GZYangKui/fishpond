package cn.navclub.fishpond.server.util;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Tuple;


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
}
