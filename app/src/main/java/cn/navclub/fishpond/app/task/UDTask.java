package cn.navclub.fishpond.app.task;


import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public abstract class UDTask<T> implements Runnable {

    private final Task task;
    protected final Logger logger;
    private final List<TSubscribe<T>> subscribes;
    private final AtomicReference<TKStatus> tkStatus;


    public UDTask(Task task) {
        this.task = task;
        this.subscribes = new ArrayList<>();
        this.tkStatus = new AtomicReference<>(TKStatus.RUNNING);
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
        synchronized (this) {
            this.subscribes.remove(subscribe);
        }
    }

    /**
     * 发布任务完成
     */
    private void onComplete(T item) {
        synchronized (this) {
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
    }

    /**
     * 发布错误
     */
    private void onError(Throwable t) {
        synchronized (this) {
            for (TSubscribe<T> subscribe : this.subscribes) {
                try {
                    subscribe.onError(t);
                } catch (Exception e) {
                    logger.error("OnError task happen error:{}", e.getMessage());
                }
            }
            //移出所有订阅订阅者
            this.subscribes.clear();
        }
    }

    protected final void onProgress(long delta,long send, long total) {
        synchronized (this) {
            for (TSubscribe<T> subscribe : this.subscribes) {
                try {
                    subscribe.progress(delta,send, total);
                } catch (Exception e) {
                    logger.error("OnProgress task happen error:{}", e.getMessage());
                }
            }
        }
    }


    @Override
    public void run() {
        T item = null;
        Throwable t = null;
        try {
            item = this.run0();
        } catch (Exception e) {
            t = e;
            logger.info("Task execute happen error!", e);
        }

        if (t != null) {
            this.onError(t);
            this.setStatus(TKStatus.STOP);
        } else {
            this.onComplete(item);
            this.setStatus(TKStatus.EXIT);
        }
    }

    protected void setStatus(TKStatus newStatus) {
        synchronized (this) {
            var oldStatus = this.tkStatus.getAndSet(newStatus);
            for (TSubscribe<T> subscribe : this.subscribes) {
                try {
                    subscribe.statusChange(oldStatus, newStatus);
                } catch (Exception ignore) {
                }
            }
        }
    }

    /**
     * 返回当前任务状态
     */
    public TKStatus getTkStatus() {
        return this.tkStatus.get();
    }


    protected abstract T run0() throws Exception;

    /**
     * 任务状态
     */
    public enum TKStatus {
        /**
         * 等待执行
         */
        WAIT,
        /**
         * 运行中
         */
        RUNNING,
        /**
         * 任务暂停
         */
        PAUSE,
        /**
         * 任务取消
         */
        CANCEL,
        /**
         * 任务终止(错误造成)
         */
        STOP,
        /**
         * 任务停止(正常终止)
         */
        EXIT
    }

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
