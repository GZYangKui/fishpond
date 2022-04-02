package cn.navclub.fishpond.server.config;

import cn.navclub.fishpond.core.worker.SnowFlakeWorker;

/**
 * 全局ID生成器
 */
public class GlobalIDGen {
    public final SnowFlakeWorker snowFlakeWorker;

    public GlobalIDGen(final long robotId) {
        this.snowFlakeWorker = new SnowFlakeWorker(robotId);
    }

    public long globalId() {
        return this.snowFlakeWorker.generateId();
    }

    private static GlobalIDGen idGen;

    public static synchronized GlobalIDGen createGen(long robotId) {
        if (idGen == null) {
            idGen = new GlobalIDGen(robotId);
        }
        return idGen;
    }

    public static GlobalIDGen getInstance() {
        if (idGen == null) {
            throw new RuntimeException("Please call createGen method construction instance after call this method!");
        }
        return idGen;
    }
}
