package cn.navclub.fishpond.app.task;

/**
 * 任务订阅
 *
 * @param <T> 任务结果
 */
public interface TSubscribe<T> {
    /**
     *
     * 错误发生是回调该函数
     *
     * @param t 异常信息
     *
     */
    void onError(Throwable t);

    /**
     *
     * 任务进度发生改变时回调该函数
     *
     * @param delta 进度改变值
     * @param total 总进度
     */
    void progress(long delta,long total);

    /**
     *
     * 任务完成回调该函数
     *
     * @param item 任务执行结果
     */
    void complete(T item);

    /**
     *
     * 任务状态改变
     *
     */
    void statusChange(UDTask.TKStatus oldStatus,UDTask.TKStatus newStatus);
}
