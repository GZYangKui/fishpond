module cn.navclub.fishpond.server {
    requires io.vertx.core;
    requires kotlin.stdlib;
    requires io.vertx.web;
    requires com.fasterxml.jackson.databind;
    requires io.vertx.client.sql.mysql;
    requires io.vertx.kotlin.coroutines;
    requires cn.navclub.fishpond.protocol;
}