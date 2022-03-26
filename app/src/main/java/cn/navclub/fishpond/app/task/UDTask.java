package cn.navclub.fishpond.app.task;


import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class UDTask<T> implements Runnable {

    private final Task task;
    private final List<TSubscribe<T>> subscribes;

    protected final Logger logger;

    public UDTask(Task task) {
        this.task = task;
        this.subscribes = new ArrayList<>();
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * 订阅任务
     */
    public final void subscribe(TSubscribe<T> subscribe) {
        synchronized (this) {
            if (this.subscribes.contains(subscribe)) {
                return;
            }
            this.subscribes.add(subscribe);
        }
    }

    /**
     * 取消任务订阅
     */
    public final void unsubscribe(TSubscribe<T> subscribe) {
        this.subscribes.remove(subscribe);
    }

    /**
     * 发布任务完成
     */
    private void onComplete(T item) {
        for (TSubscribe<T> subscribe : this.subscribes) {
            try {
                subscribe.complete(item);
            } catch (Exception e) {
                logger.error("Complete task happen error:{}", e.getMessage());
            }
        }
        //移出所有订阅订阅者
        this.subscribes.clear();
    }

    /**
     * 发布错误
     */
    private void onError(Throwable t) {
        for (TSubscribe<T> subscribe : this.subscribes) {
            try {
                subscribe.onError(t);
            } catch (Exception e) {
                logger.error("OnError task happen error:{}", e.getMessage());
            }
        }
        this.subscribes.clear();
    }

    protected final void onProgress(long delta, long total) {
        for (TSubscribe<T> subscribe : this.subscribes) {
            try {
                subscribe.progress(delta, total);
            } catch (Exception e) {
                logger.error("OnProgress task happen error:{}", e.getMessage());
            }
        }
    }


    @Override
    public void run() {
        try {
            this.onComplete(this.run0());
        } catch (Exception e) {
            this.onError(e);
        }
    }

    protected abstract T run0() throws Exception;

    public enum Task {
        /**
         * 上传
         */
        UPLOAD,
        /**
         * 下载
         */
        DOWNLOAD,
    }
}
