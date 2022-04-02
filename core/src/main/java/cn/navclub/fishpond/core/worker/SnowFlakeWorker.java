package cn.navclub.fishpond.core.worker;

/**
 * 雪花ID生成算法实现
 *
 * @author yangkui
 */
public class SnowFlakeWorker {
    /**
     * 时间戳左移位数
     */
    private static final long TIME_SHIFT = 22;
    /**
     * 机器标识左移位数
     */
    private static final long ROBOT_ID_SHIFT = 12;
    /**
     * 允许最大自增序号
     */
    private static final long MAX_SERIAL_NUM = (~0L) >>> 52;

    private long serialNo;

    private long lastMillTime;

    private final long robotId;

    public SnowFlakeWorker(long robotId) {
        this.serialNo = -1;
        this.robotId = robotId;
        this.lastMillTime = System.currentTimeMillis();
    }


    public synchronized long generateId() {
        var id = 0L;
        if (serialNo > MAX_SERIAL_NUM) {
            serialNo = -1;
            this.lastMillTime = this.currentMillTime();
        }
        serialNo = serialNo + 1;

        return id | serialNo | (robotId << ROBOT_ID_SHIFT) | (lastMillTime << TIME_SHIFT);
    }

    /**
     * 获取时间戳
     */
    private long currentMillTime() {
        while (lastMillTime != 0 && lastMillTime == System.currentTimeMillis()) {
            //Not any operation
        }
        return System.currentTimeMillis();
    }
}
