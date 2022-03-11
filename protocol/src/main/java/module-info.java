module cn.navclub.fishpond.protocol {
    requires io.vertx.core;
    requires static lombok;
    requires cn.navclub.fishpond.core;
    exports cn.navclub.fishpond.protocol;
    exports cn.navclub.fishpond.protocol.util;
    exports cn.navclub.fishpond.protocol.enums;
    exports cn.navclub.fishpond.protocol.model;
    exports cn.navclub.fishpond.protocol.impl;
    exports cn.navclub.fishpond.protocol.api;
}