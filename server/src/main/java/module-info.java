module cn.navclub.fishpond.server {
    requires io.vertx.core;
    requires kotlin.stdlib;
    requires io.vertx.web;
    requires java.desktop;
    requires io.vertx.client.sql;
    requires io.vertx.client.mail;
    requires io.vertx.client.redis;
    requires cn.navclub.fishpond.core;
    requires io.vertx.client.sql.mysql;
    requires io.vertx.kotlin.coroutines;
    requires cn.navclub.fishpond.protocol;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
}