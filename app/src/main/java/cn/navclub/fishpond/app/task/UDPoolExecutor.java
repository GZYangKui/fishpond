package cn.navclub.fishpond.app.task;


import java.util.concurrent.*;

/**
 * 上传/下载文件执行器
 */
public class UDPoolExecutor extends ThreadPoolExecutor {
    private UDPoolExecutor() {
        super(0, 2, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
    }

    private static UDPoolExecutor UDPoolExecutor;

    public static synchronized UDPoolExecutor getInstance() {
        if (UDPoolExecutor == null) {
            UDPoolExecutor = new UDPoolExecutor();
        }
        return UDPoolExecutor;
    }
}
